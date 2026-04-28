# 用户端聊天界面
FROM node:18-alpine AS builder

WORKDIR /app

COPY package.json pnpm-lock.yaml ./
COPY packages/user-chat/package.json ./packages/user-chat/
COPY shared/package.json ./shared/

RUN npm install -g pnpm && pnpm install --frozen-lockfile

COPY . .

RUN cd packages/user-chat && pnpm build

FROM nginx:alpine

COPY --from=builder /app/packages/user-chat/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
