VERSION = $$(git rev-parse --short=10 HEAD)
IMAGE = europe-north1-docker.pkg.dev/artifacts-352708/mat/digitavlen:$(VERSION)

docker/build:
	clojure -X:build

docker: docker/build
	cd docker && docker build -t $(IMAGE) .

publish:
	docker push $(IMAGE)

test:
	bin/kaocha

clean:
	rm -fr docker/build

.PHONY: clean docker publish test
