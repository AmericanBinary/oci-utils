.PHONY: all

all: build

.PHONY: requirements
requirements:
	poetry install

.PHONY: serve
serve: requirements
	poetry run mkdocs serve

.PHONY: build
build: requirements
	poetry run mkdocs build
