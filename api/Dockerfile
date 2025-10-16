# 1. Adım: Temel olarak ne kullanacağımızı belirtiyoruz.
# Bizim için bu, içinde Java 17 olan minimal bir işletim sistemi.
FROM openjdk:17-jdk-slim

# 2. Adım: Projemizin derlenmiş halini (JAR dosyası) argüman olarak tanımlıyoruz.
# Maven, derleme sonrası JAR dosyasını 'target' klasörüne koyar.
ARG JAR_FILE=target/*.jar

# 3. Adım: Derlenmiş JAR dosyasını bilgisayarımızdan konteynerin içine kopyalıyoruz.
# Ve adını 'app.jar' olarak değiştiriyoruz ki her zaman sabit bir isme sahip olsun.
COPY ${JAR_FILE} app.jar

# 4. Adım: Konteynerin dış dünyadan hangi port üzerinden iletişim kuracağını belirtiyoruz.
# Spring Boot varsayılan olarak 8080 portunda çalışır.
EXPOSE 8080

# 5. Adım: Konteyner başlatıldığında çalıştırılacak olan asıl komut.
# "java -jar app.jar" komutu ile Spring Boot uygulamamızı başlatıyoruz.
ENTRYPOINT ["java","-jar","/app.jar"]