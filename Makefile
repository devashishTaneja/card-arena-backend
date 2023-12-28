server:
	./gradlew build
	docker stop cardbackend
	docker rm cardbackend
	docker build -t cardbackend .
	docker run -p 8080:8080 --name=cardbackend  cardbackend

stop:
	docker stop cardbackend
	docker rm cardbackend

start-local:
	./gradlew build
	java -jar build/libs/card_arena-1.0.0.jar