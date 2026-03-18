# OrderNest API Gateway

Edge gateway for OrderNest with centralized auth at ingress.

## What It Does
- Routes API traffic to backend services.
- Validates JWT access tokens at edge using SSO JWKS.
- Applies role-based access control for shipment admin endpoint.

## Run
```bash
./gradlew bootRun
```

## Key Env Vars
- `SSO_JWKS_URL`
- `ROUTE_SSO_BASE_URL`
- `ROUTE_INVENTORY_BASE_URL`
- `ROUTE_ORDER_BASE_URL`
- `ROUTE_PAYMENT_BASE_URL`
- `ROUTE_SHIPMENT_BASE_URL`

## Gateway Endpoints
- `POST /auth/register` -> SSO service
- `POST /auth/login` -> SSO service
- `GET /api/products/**` -> inventory
- `POST/GET /api/orders/**` -> order
- `POST /api/payments/**` -> payment
- `POST /api/shipments/status` -> order-service shipment workflow (ADMIN only)
