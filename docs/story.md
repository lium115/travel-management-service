# api定义

## 提供api
```
POST /rental-delegation-contracts/{cid}/payment-request
```

request body:
```json
{
  "agent_id": "bigint"
}
```

happy path response:
```json
{
  "payment_id": "xxx",
  "payment_link": "https://demo.payment.com/payments/12345",
  "amount": 2022.33,
  "expired_at": "2022-03-26 20:30:00"
}
```

## 调用支付网关api

```
POST /payments
```

request body:
```json
{
  "payment_id": "xxx",
  "amount": 2022.33
}
```
happy path response:
status code 200, body:
```json
{
  "payment_id": "xxx",
  "payment_link": "https://demo.payment.com/payments/12345",
  "amount": 2022.33,
  "expired_at": "2022-03-26 20:30:00"
}
```

# 数据库表

## rental_delegation_contract
```json
{
  "id": "bigint",
  "month_price": "decimal",
  "client_id": "bigint",
  "agent_id": "bigint",
  "house_id": "bigint",
  "created_at": "datetime"
}
```

## commission_payment
```json
{
  "id": "unchar",
  "contract_id": "bigint",
  "payment_link": "unchar",
  "created_at": "datetime",
  "expired_at": "datetime"
}
```

# Story

## AC1(happy path)

根据`cid`查找contract，获取其月租金，使用半月房租价格作为佣金金额，生成交易流水号，调用支付网关POST api，传入流水号、金额，返回结果`payment_link`，存入payment表。

## AC2(分区)

在调用支付网关api时，不管POST结果，使用GET方法查询，若GET失败，则间隔10s重试，最多重试3次。

## AC3(invalid request)

不合法的request，返回400和error message

### Example1

不存在的`cid`

### Example2

`agent_id`不匹配


# 工序

## 工序1，repository + entity

## 工序2，feign client

## 工序3，mq

## 工序4，service

## 工序5，controller

## 工序6，集成
