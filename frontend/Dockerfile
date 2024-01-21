FROM registry.access.redhat.com/ubi9/nginx-122:1-45

ADD dist .

COPY ./nginx-config/nginx.conf /etc/nginx/

CMD nginx -g "daemon off;"
