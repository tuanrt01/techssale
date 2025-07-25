# Build stage
FROM node:20.13.1-alpine as build

WORKDIR /app
COPY ./ ./
RUN npm install --verbose
RUN npm run build

# Run production
FROM nginx:alpine as production

COPY --from=build /app/dist /usr/share/nginx/html
COPY ./nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]