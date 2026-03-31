FROM mysql:8.0.45

ARG AIVEN_DATABASE
ARG AIVEN_UID
ARG AIVEN_PWD
ARG AIVEN_PWD

# 設定環境變數
ENV MYSQL_DATABASE=${AIVEN_DATABASE}
ENV MYSQL_USER=${AIVEN_UID}
ENV MYSQL_PASSWORD=${AIVEN_PWD}
ENV MYSQL_ROOT_PASSWORD=${AIVEN_PWD}

# COPY my.cnf /etc/my.cnf
RUN echo '[mysqld]' >> /etc/my.cnf  \
    && echo "general_log = 1" >> /etc/my.cnf \
    && echo "general_log_file = /var/log/mysqld.log" >> /etc/my.cnf \
    && echo "log_output = file" >> /etc/my.cnf
    
# 指定路徑
WORKDIR /var/lib/mysql

# 指定容器運行時使用的預設命令
CMD ["mysqld"]