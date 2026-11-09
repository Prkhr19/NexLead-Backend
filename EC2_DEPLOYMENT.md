# Deploying NexLead on one EC2 instance

This setup runs the Spring Boot API, PostgreSQL, and Redis with Docker Compose. PostgreSQL and Redis are private to the Compose network; the API binds to `127.0.0.1:8080` on the EC2 host so a host reverse proxy can serve HTTPS.

## 1. Prepare the instance

- Create an Ubuntu 24.04 LTS EC2 instance. A `t3.small` is a practical starting point; size up if memory pressure appears.
- Attach a security group allowing SSH (22) only from your IP, and HTTP/HTTPS (80/443) from the internet. Do not open 5432, 6379, or 8080.
- Allocate and associate an Elastic IP if you need a stable address. Point your domain's DNS A record at it.
- Install Docker Engine and the Docker Compose plugin using Docker's official Ubuntu instructions.

## 2. Get the code and configure secrets

On the instance, clone this repository into `/opt/nexlead`, then:

```sh
cd /opt/nexlead
cp .env.example .env
```

Edit `.env` and set unique, long passwords. Generate `JWT_SECRET` as Base64 for at least 32 random bytes, for example with `openssl rand -base64 32`. Keep `.env` private and never commit it. The configured admin account is seeded when the database has no users.

## 3. Start the stack

```sh
docker compose -f compose.prod.yaml up -d --build
docker compose -f compose.prod.yaml ps
docker compose -f compose.prod.yaml logs -f app
```

The API should answer locally at `http://127.0.0.1:8080/api-docs`. Configure Nginx or Caddy on the host to proxy your domain to `127.0.0.1:8080` and obtain TLS certificates. Keep the API's public traffic behind that proxy.

## 4. Updates and data

Back up PostgreSQL before upgrades. Pull the desired revision and run `docker compose -f compose.prod.yaml up -d --build`. Persistent Docker volumes keep PostgreSQL and Redis data across container replacements. `DDL_AUTO=update` avoids dropping the schema on each restart, but is not a substitute for versioned database migrations; switch to Flyway/Liquibase and `validate` before relying on automated production schema changes. The current `create` behavior was destructive on app restarts, so the application default is now `update`.

## Before exposing production traffic

- Confirm the instance can reach the repository and package/image sources.
- Add automated off-instance PostgreSQL backups and test restoring one.
- Set up monitoring, log retention, OS updates, and a recovery path for the EC2 instance.
- Review CORS and allowed origins when the frontend domain is known.
- Use a managed database such as RDS for stronger backup and availability guarantees as the service grows.
