# About
This is a performatic, highly-scalable notification app made with Kotlin and Typescript.

# Tech stack
- Backend:
  - Micronaut + Kotlin
- Frontend:
  - React + Typescript
- DB:
  - Postgres
- Cloud:
  - AWS (using Localstack)
- Infra:
  - Docker
  - Github Actions

---

# How to run? 

To run this project, you'll need to install:
- Git
- Docker

Go to the folder of the cloned project, in the root folder type:
```
docker-compose up -d --build
```

The URLs are:

Frontend:
```
http://localhost:3000
```

Backend:
```
http://localhost:8000/api/v1/notifications
```
