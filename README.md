# MoneyExchange

In the application.yml file Please write credentials that you want to use. There are available two endpoints:

- rate-euro-endpoint - is the endpoint for free use endpoint, where euro is base and service will compute rate using data from base Euro. You can use this not only for Euro. 

- rate-query-endpoint - it is a dedicated endpoint to get the result using API only.

1. You can disable endpoint using in configuration field `enable` {true or false}.
2. if neither endpoint is set, it will throw the error `All exchange endpoints are disabled`
3. If two endpoints are enabled, it will work the only endpoint `rate-euro-endpoint`
```
exchange:
  http-client-config:
    max-idle-connections: {{number_of_idle_connections_in_the_pool}}
    keep-alive-duration: {{keep_alive_duration}}
    connect-timeout: {{connect_timeout}}
  credentials:
    rate-euro-endpoint:
          enable : {{ true or false }}
          host: http://api.exchangeratesapi.io/v1/latest?access_key={access_key}
          access-key: {{access_key}}
    rate-query-endpoint:
          enable : {{ true or false }}
          host: https://api.exchangeratesapi.io/v1/latest?access_key={access_key}&base={source}&symbols={target}
          access-key: {{access_key}}
```

Please use ready-to-use postman file.