# Étape 1 : Build Angular app
FROM node:20-alpine AS builder
WORKDIR /app
COPY . .
RUN npm install
RUN npm run build -- --output-path=dist

# Étape 2 : Serve l'app via nginx
FROM nginx:alpine
COPY --from=builder /app/dist/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf