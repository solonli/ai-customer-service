# 管理后台界面
FROM node:18-alpine AS builder

WORKDIR /app

COPY package.json pnpm-lock.yaml ./
COPY packages/admin-console/package.json ./packages/admin-console/
COPY shared/package.json ./shared/

RUN npm install -g pnpm && pnpm install --frozen-lockfile

COPY . .

RUN cd packages/admin-console && pnpm build

FROM nginx:alpine

COPY --from=builder /app/packages/admin-console/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
