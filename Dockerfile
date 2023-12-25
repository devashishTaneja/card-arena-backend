FROM openjdk:17-alpine
RUN mkdir -p /card_arena
COPY build/libs/card_arena-1.0.0.jar /card_arena/card_arena.jar
ENTRYPOINT ["java", "-jar", "/card_arena/card_arena.jar", "--spring.profiles.active=prod"]