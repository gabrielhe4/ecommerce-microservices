# ecommerce-microservices
Ecommerce with microservices architecture


## Generate the RSA key pair

Run these once on your machine to create the key files. You'll place the private key in auth-service and the public key in the gateway.

Generate a 2048-bit private key:
`openssl genrsa -out private.pem 2048`

extract the public key from it:
`openssl rsa -in private.pem -pubout -out public.pem`

convert the private key to PKCS#8 format (what Java expects)
`openssl pkcs8 -topk8 -inform PEM -in private.pem -out private_pkcs8.pem -nocrypt`

You'll end up with three files. Use private_pkcs8.pem (Java-friendly private key) and public.pem. The plain private.pem you can discard.

Place them like this:

```
auth-service/src/main/resources/keys/private_pkcs8.pem   ← auth-service signs with this
api-gateway/src/main/resources/keys/public.pem           ← gateway verifies with this

```

Important note: Never commit the private key to a real repository. It's a security risk.