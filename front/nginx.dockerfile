FROM nginx:1.26.3

ENV TZ="Asia/Taipei"
EXPOSE 80/TCP
EXPOSE 6173/TCP

ARG config_file=nginx.conf
COPY ./${config_file} /etc/nginx/nginx.conf

ARG publish_dir=dist
COPY ./${publish_dir} /usr/share/nginx/html
COPY ./50x.html /usr/share/nginx/html

CMD ["nginx", "-g", "daemon off;"]
