.PHONY: front-start back-build back-start

db-start:
	docker-compose -f docker-compose-db.yml up -d

db-stop:
	docker compose -f docker-compose-db.yml stop

front-start:
	cd frontend && npm run dev

back-build:
	cd backend && ./gradlew build

back-start:
	cd backend && ./gradlew bootRun
