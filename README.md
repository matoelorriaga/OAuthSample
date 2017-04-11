# OAuthSample

Based on this great blog post: 

http://www.hascode.com/2016/03/setting-up-an-oauth2-authorization-server-and-resource-provider-with-spring-boot/

## Running the Identity Server

```
cd identity-server && mvn spring-boot:run
```

## Running the Resource Provider

```
cd resource-provider && mvn spring-boot:run
```

## Accessing the secured Resource (not yet authorized)

```
curl -X GET http://localhost:9001/resource
```

## Requesting a Token

```
curl -X POST http://localhost:9000/oauthsample/oauth/token -H 'authorization: Basic Y2xpZW50OnNlY3JldA==' -d 'grant_type=password&client_id=client&client_secret=secret&redirect_uri=http://github.com/matoelorriaga&username=matias&password=abc123'
```

## Accessing the secured Resource

```
curl -H "Authorization: Bearer a0fb9e22-ec6e-436f-9cd3-de0460ccbcb3" http://localhost:9001/resource/
```
