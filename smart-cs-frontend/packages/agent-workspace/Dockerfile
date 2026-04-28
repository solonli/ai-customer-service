# 客服工作台界面
FROM node:18-alpine AS builder

WORKDIR /app

COPY package.json pnpm-lock.yaml ./
COPY packages/agent-workspace/package.json ./packages/agent-workspace/
COPY shared/package.json ./shared/

RUN npm install -g pnpm && pnpm install --frozen-lockfile

COPY . .

RUN cd packages/agent-workspace && pnpm build

FROM nginx:alpine

COPY --from=builder /app/packages/agent-workspace/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
