FROM amazoncorretto:17

# Указываем рабочую директорию
WORKDIR /app

# Копируем файл сборки JAR в контейнер
COPY build/libs/SockManager-1.0-SNAPSHOT.jar app.jar

# Указываем порт, который будет использовать приложение
EXPOSE 8080

# Команда для запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]