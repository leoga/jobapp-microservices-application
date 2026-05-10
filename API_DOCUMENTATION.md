# API Documentation - Job Application Microservices Platform

RESTful API for managing job offers, companies, and reviews using a microservices architecture.

## Base URLs

- Job service: `http://localhost:8081/jobs`
- Company service: `http://localhost:8082/companies`
- Review service: `http://localhost:8083/reviews`
- Gateway API: `http://localhost:8080`

---

# 📑 Table of Contents

1. [Jobs](#-jobs)
2. [Companies](#-companies)
3. [Reviews](#-reviews)
4. [DTO Models](#-dto-models)

---

# 💼 Jobs

## Create Job

```http
POST /jobs
```

Creates a new job offer.

> The provided `companyId` must exist in the Company Service.

### Request Body

```json
{
  "title": "Senior Java Developer",
  "description": "Backend developer with Spring Boot experience",
  "minSalary": "45000",
  "maxSalary": "70000",
  "location": "Barcelona",
  "companyId": 1
}
```

### Successful Response (201 CREATED)

```json
{
  "id": 1,
  "title": "Senior Java Developer",
  "description": "Backend developer with Spring Boot experience",
  "minSalary": "45000",
  "maxSalary": "70000",
  "location": "Barcelona",
  "company": {
    "id": 1,
    "name": "Tech Solutions",
    "description": "Software consulting company",
    "rating": null,
    "reviews": []
  }
}
```

### Error Response (400 BAD REQUEST)

```json
"Company not found"
```

---

## Get All Jobs

```http
GET /jobs
```

Returns all available job offers.

### Successful Response (200 OK)

```json
[
  {
    "id": 1,
    "title": "Senior Java Developer",
    "description": "Backend developer with Spring Boot experience",
    "minSalary": "45000",
    "maxSalary": "70000",
    "location": "Barcelona",
    "company": {
      "id": 1,
      "name": "Tech Solutions",
      "description": "Software consulting company",
      "rating": null,
      "reviews": []
    }
  }
]
```

---

## Get Job by ID

```http
GET /jobs/{id}
```



### Path Parameters

- `id` (`Long`) - Job ID

### Successful Response (200 OK)

```json
 
{
  "id": 1,
  "title": "Senior Java Developer",
  "description": "Backend developer with Spring Boot experience",
  "minSalary": "45000",
  "maxSalary": "70000",
  "location": "Barcelona",
  "company": {
    "id": 1,
    "name": "Tech Solutions",
    "description": "Software consulting company",
    "rating": null,
    "reviews": []
  }
}
 
```

### Error Response (404 NOT FOUND)

```json
"Job not found"
```

---

## Update Job

```http
PATCH /jobs/{id}
```


Updates an existing job offer.


> Only the provided fields will be updated.

### Path Parameters

- `id` (`Long`) - Job ID

### Request Body

```json
{
  "maxSalary": "80000"
}
```

### Successful Response (200 OK)

```json
 
{
  "id": 1,
  "title": "Senior Java Developer",
  "description": "Backend developer with Spring Boot experience",
  "minSalary": "45000",
  "maxSalary": "80000",
  "location": "Barcelona",
  "company": {
    "id": 1,
    "name": "Tech Solutions",
    "description": "Software consulting company",
    "rating": null,
    "reviews": []
  }
}
 
```

### Error Response (404 NOT FOUND)

```json
"Job not found"
```

---

## Delete Job

```http
DELETE /jobs/{id}
```



Deletes a job offer by ID.

### Path Parameters

- `id` (`Long`) - Job ID

### Successful Response (200 OK)

```json
"Job deleted successfully"
```

### Error Response (404 NOT FOUND)

```json
"Job not found"
```

---

# 🏢 Companies

## Create Company

```http
POST /companies
```

Creates a new company.

### Request Body

```json
{
  "name": "Tech Solutions",
  "description": "Software consulting company"
}
```

### Successful Response (201 CREATED)

```json
{
  "id": 1,
  "name": "Tech Solutions",
  "description": "Software consulting company",
  "rating": null,
  "reviews": []
}
```

---

## Get All Companies

```http
GET /companies
```

Returns all registered companies.

### Successful Response (200 OK)

```json
[
  {
    "id": 1,
    "name": "Tech Solutions",
    "description": "Software consulting company",
    "rating": null,
    "reviews": []
  }
]
```

---

## Get Company by ID

```http
GET /companies/{id}
```



### Path Parameters

- `id` (`Long`) - Company ID

### Successful Response (200 OK)

```json
{
  "id": 1,
  "name": "Tech Solutions",
  "description": "Software consulting company",
  "rating": null,
  "reviews": []
}
```

### Error Response (404 NOT FOUND)

```json
"Company not found"
```

---

## Update Company

```http
PATCH /companies/{id}
```



Updates an existing company.

> Only the provided fields will be updated.

### Path Parameters

- `id` (`Long`) - Company ID

### Request Body

```json
{
  "name": "Tech Solutions Updated"
}
```

### Successful Response (200 OK)

```json
{
  "id": 1,
  "name": "Tech Solutions Updated",
  "description": "Software consulting company",
  "rating": null,
  "reviews": []
}
```

### Error Response (404 NOT FOUND)

```json
"Company not found"
```

---

## Update Company Rating

```http
PUT /companies/{id}
```



Updates the company rating based on its reviews.

> The rating is asynchronously updated through Kafka events when a review is created.  
> This endpoint allows the rating to be recalculated directly from the Company Service.

### Path Parameters

- `id` (`Long`) - Company ID

### Successful Response (200 OK)

```json
"Company rating updated successfully"
```

### Error Response (404 NOT FOUND)

```json
"Company not found"
```

---

## Delete Company

```http
DELETE /companies/{id}
```

Deletes a company by ID.


### Path Parameters

- `id` (`Long`) - Company ID

### Successful Response (200 OK)

```json
"Company deleted successfully"
```

### Error Response (404 NOT FOUND)

```json
"Company not found"
```

---

# ⭐ Reviews

## Create Review

```http
POST /reviews
```

Creates a new review for a company.

### Request Body

```json
{
  "companyId": 1,
  "title": "Great company",
  "description": "Excellent work environment",
  "rating": 5.0
}
```

### Successful Response (201 CREATED)

```json
"Review created successfully"
```

---

## Get Reviews by Company

```http
GET /reviews?companyId=1
```



Returns all reviews for the specified company.

### Query Parameters

- `companyId` (`Long`) - Company ID

### Successful Response (200 OK)

```json
[
  {
    "id": 1,
    "companyId": 1,
    "title": "Great company",
    "description": "Excellent work environment",
    "rating": 5.0
  }
]
```

---

## Get Review by ID

```http
GET /reviews/{reviewId}
```



### Path Parameters

- `reviewId` (`Long`) - Review ID

### Successful Response (200 OK)

```json
{
  "id": 1,
  "companyId": 1,
  "title": "Great company",
  "description": "Excellent work environment",
  "rating": 5.0
}
```

### Error Response (404 NOT FOUND)

```json
"Review not found"
```

---

## Update Review

```http
PATCH /reviews/{reviewId}
```



Updates an existing review.

> Only the provided fields will be updated.

### Path Parameters

- `reviewId` (`Long`) - Review ID

### Request Body

```json
{
  "companyId": 1,
  "title": "Great company updated",
  "description": "Excellent work environment",
  "rating": 5.0
}
```

### Successful Response (200 OK)

```json
"Review updated successfully"
```

### Error Response (400 BAD REQUEST)

```json
"Company Not Found or Review Not Found"
```

---

## Delete Review

```http
DELETE /reviews/{reviewId}
```

Deletes a review by ID.

### Path Parameters

- `reviewId` (`Long`) - Review ID

### Successful Response (200 OK)

```json
"Review deleted successfully"
```

### Error Response (404 NOT FOUND)

```json
"Review not found"
```

---

## Get Average Rating

```http
GET /reviews/averageRating?companyId=1
```

Returns the average rating for the specified company.

> If the company does not exist or has no reviews, the endpoint returns `0.0`.

### Query Parameters

- `companyId` (`Long`) - Company ID

### Successful Response (200 OK)

```json
4.5
```

---

# 📝 DTO Models

## JobRequest

```json
{
  "title": "String",
  "description": "String",
  "minSalary": "String",
  "maxSalary": "String",
  "location": "String",
  "companyId": "Long"
}
```

---

## CompanyRequest

```json
{
  "name": "String",
  "description": "String"
}
```

---

## ReviewRequest

```json
{
  "companyId": "Long",
  "title": "String",
  "description": "String",
  "rating": "Double"
}
```

---

## JobResponse

```json
{
  "id": "Long",
  "title": "String",
  "description": "String",
  "minSalary": "String",
  "maxSalary": "String",
  "location": "String",
  "company": "CompanyResponse"
}
```

---

## CompanyResponse

```json
{
  "id": "Long",
  "name": "String",
  "description": "String",
  "rating": "Double",
  "reviews": "List<ReviewResponse>"
}
```

---

## ReviewResponse

```json
{
  "id": "Long",
  "companyId": "Long",
  "title": "String",
  "description": "String",
  "rating": "Double"
}
```