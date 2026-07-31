GET  /api/products          → public (anyone browses)
GET  /api/products/{id}     → public
GET  /api/categories        → public

POST   /api/admin/products      → admin only
PUT    /api/admin/products/{id} → admin only
DELETE /api/admin/products/{id} → admin only
POST   /api/admin/categories    → admin only