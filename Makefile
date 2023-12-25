build-run:
	docker build -t cardbackend .
	docker run -d -p 8080:8080 cardbackend