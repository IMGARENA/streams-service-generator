![main-test-release](https://github.com/IMGARENA/dge-sherlock/actions/workflows/main-test-release.yaml/badge.svg)
![semantic-release](https://img.shields.io/badge/%20%20%F0%9F%93%A6%F0%9F%9A%80-semantic--release-e10079.svg)

# dge-sherlock

Orchestrator for managing AWS Elemental Streaming Pipelines

## Local Execution
### Command line

For running from command line, make sure you [installed the direnv shell extension](https://imgarenahub.atlassian.net/wiki/spaces/IAT/pages/1941536794/.env+Files), and use the spring-boot maven plugin.

Ensure that you have a valid AWS SSO token

```shell
aws sso login
```

```
./run.sh
```

The Swagger will be available at - http://localhost:9221/swagger-ui/index.html

## Authorisation

The service expects the `x-base64-token` header to be populated and will use the provided token to perform authorisation.

The token should be a Base64-encoded JSON blob containing the user's information.

#### Example JSON

```text
x-base64-token: ewogICAgIm5hbWUiOiAiU2hlcmxvY2sgSG9sbWVzIiwKICAgICJpZCI6IDIyMTgsCiAgICAicm9sZSI6ICJTVVBFUl9BRE1JTiIsCiAgICAiZW1haWwiOiAic2hlcmxvY2suaG9sbWVzQGltZ2FyZW5hLmNvbSIsCiAgICAib3BlcmF0b3JJZCI6IG51bGwsCiAgICAicGVybWlzc2lvbnMiOiBbXQp9
```

Is the base64 encoded token for the following authz details.

```json
{
    "name": "Sherlock Holmes",
    "id": 2218,
    "role": "SUPER_ADMIN",
    "email": "sherlock.holmes@imgarena.com",
    "operatorId": null,
    "permissions": []
}
```

#### Example requests (replace pipeline key & akamai url)
```shell
curl --request PUT \
  --url http://localhost:9221/api/v1/pipelines/eu-west-1/<your-pipeline-key-here> \
  --header 'Content-Type: application/json' \
  --header 'x-base64-token: ewogICAgIm5hbWUiOiAiU2hlcmxvY2sgSG9sbWVzIiwKICAgICJpZCI6IDIyMTgsCiAgICAicm9sZSI6ICJTVVBFUl9BRE1JTiIsCiAgICAiZW1haWwiOiAic2hlcmxvY2suaG9sbWVzQGltZ2FyZW5hLmNvbSIsCiAgICAib3BlcmF0b3JJZCI6IG51bGwsCiAgICAicGVybWlzc2lvbnMiOiBbXQp9' \
  --data '{
	"destinationUrls": {
		"primary": "https://p-ep2023903.i.akamaientrypoint.net/2023903/{EVENT_ANGLE}/playlist"
	}
}'
```

```shell
curl --request DELETE \
  --url http://localhost:9221/api/v1/pipelines/eu-west-1/<your-pipeline-key-here> \
  --header 'Content-Type: application/json' \
  --header 'x-base64-token: ewogICAgIm5hbWUiOiAiU2hlcmxvY2sgSG9sbWVzIiwKICAgICJpZCI6IDIyMTgsCiAgICAicm9sZSI6ICJTVVBFUl9BRE1JTiIsCiAgICAiZW1haWwiOiAic2hlcmxvY2suaG9sbWVzQGltZ2FyZW5hLmNvbSIsCiAgICAib3BlcmF0b3JJZCI6IG51bGwsCiAgICAicGVybWlzc2lvbnMiOiBbXQp9'
```



## Code Formatting

We have adopted the [Google Java Style guide](https://google.github.io/styleguide/javaguide.html)

The CI pipeline will run the following and fail the build if formatting is not correct

```mvnw spotless:check```

To auto format your code run `./fmt.sh`
