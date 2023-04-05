clean:
	echo "clean application-1.0"
	rm -rf application-1.0

build-project:
	echo "clean build"
	./gradlew clean build -x test

extract:
	echo "extract new build"
	tar -xf ./application/build/distributions/application-1.0.tar

build-image:
	docker build -t myapp .

run: clean build-project extract
run-build-image: run build-image