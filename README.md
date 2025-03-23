# JSON:API

JSON:API는 클라이언트와 서버 간 리소스 요청 및 응답 방식을 정의한 표준화된 형식입니다.   
API 설계방식이 단순한 **기능 중심** -> **표준화 및 개발자 경험 중심**으로 전환되고 있습니다.   
이러한 흐름 속에서, 전통적인 REST API의 HATEOAS를 완전히 구현하는 것은 개발 복잡성과 유지보수 비용이 높다는 문제가 있었습니다.   
JSON:API는 이러한 복잡성을 줄이면서도 리소스 간 관계 표현, 페이지네이션 등을 표준화된 방식으로 제공합니다.   
또한 불필요한 데이터 전송을 최소화하고 필요한 리소스를 한 번의 요청으로 가져올 수 있어 네트워크 비용을 크게 절감할 수 있습니다.   

## 핵심 원칙

1. **리소스 중심 설계:** 모든 데이터는 '리소스'로 표현되며, 각 리소스는 고유한 유형과 ID를 가집니다. 이는 REST 원칙에 부합하는 설계입니다.
2. **관계 기반 데이터 구조화:** JSON API는 관계형 데이터베이스와 유사하게 리소스 간의 관계를 명확하게 표현합니다.
3. **클라이언트 요구 최적화:** 클라이언트가 필요한 데이터만 효율적으로 요청할 수 있도록 설계되었습니다.
   - 필드 필터링 (`fields[type]=field1,field2`)
   - 관계 포함 (`include=author,comments`)
   - 페이지네이션, 정렬, 필터링을 위한 표준 쿼리 파라미터
4. **단일 요청 최적화:** "복합 문서(Compound Document)" 패턴을 통해 관련 리소스를 하나의 응답에 포함시켜 네트워크 요청을 최소화합니다.
5. **변경 최소화 원칙:** API 업데이트 시 클라이언트 측 변경을 최소화하는 설계 지향
   - JSON:API의 새로운 버전은 항상 이전 버전과 호환될 수 있도록 `never remove, only add` 전략을 사용한다고 써져있습니다.

## 기본 구조와 요청 예제

**Content-Type/Accept 헤더**: 모든 요청과 응답에 `application/vnd.api+json`을 사용해야 합니다.

### 1. 기본 리소스 요청

**요청:**
```http request
GET /articles/1 HTTP/1.1
Host: example.com
Accept: application/vnd.api+json
```

**응답:**
```http response
HTTP/1.1 200 OK
Content-Type: application/vnd.api+json

{
  "data": {
    "type": "articles",
    "id": "1",
    "attributes": {
      "title": "JSON:API 소개",
      "content": "JSON:API는 API 응답 형식을 표준화합니다...",
      "created": "2025-01-15T12:00:00Z",
      "updated": "2025-02-10T15:30:00Z"
    },
    "links": {
      "self": "https://example.com/articles/1"
    }
  }
}
```

### 2. 특정 필드만 요청 (필드 필터링)

**요청:**
```http request
GET /articles/1?fields[articles]=title,created HTTP/1.1
Host: example.com
Accept: application/vnd.api+json
```

**응답:**
```http response
HTTP/1.1 200 OK
Content-Type: application/vnd.api+json

{
  "data": {
    "type": "articles",
    "id": "1",
    "attributes": {
      "title": "JSON:API 소개",
      "created": "2025-01-15T12:00:00Z"
    },
    "links": {
      "self": "https://example.com/articles/1"
    }
  }
}
```

### 3. 관련 리소스 포함 요청(복합 문서)

**요청:**
```http request
GET /articles/1?include=author,comments HTTP/1.1
Host: example.com
Accept: application/vnd.api+json
```

**응답:**
```http response
HTTP/1.1 200 OK
Content-Type: application/vnd.api+json

{
  "data": {
    "type": "articles",
    "id": "1",
    "attributes": {
      "title": "JSON:API 소개",
      "content": "JSON:API는 API 응답 형식을 표준화합니다...",
      "created": "2025-01-15T12:00:00Z",
      "updated": "2025-02-10T15:30:00Z"
    },
    "relationships": {
      "author": {
        "data": { "type": "people", "id": "9" },
        "links": {
          "self": "https://example.com/articles/1/relationships/author",
          "related": "https://example.com/articles/1/author"
        }
      },
      "comments": {
        "data": [
          { "type": "comments", "id": "5" },
          { "type": "comments", "id": "12" }
        ],
        "links": {
          "self": "https://example.com/articles/1/relationships/comments",
          "related": "https://example.com/articles/1/comments"
        }
      }
    },
    "links": {
      "self": "https://example.com/articles/1"
    }
  },
  "included": [
    {
      "type": "people",
      "id": "9",
      "attributes": {
        "name": "홍길동",
        "email": "hong@example.com"
      },
      "links": {
        "self": "https://example.com/people/9"
      }
    },
    {
      "type": "comments",
      "id": "5",
      "attributes": {
        "content": "좋은 글입니다!",
        "created": "2025-01-16T08:22:34Z"
      },
      "links": {
        "self": "https://example.com/comments/5"
      }
    },
    {
      "type": "comments",
      "id": "12",
      "attributes": {
        "content": "더 자세한 설명이 필요합니다.",
        "created": "2025-01-18T14:45:12Z"
      },
      "links": {
        "self": "https://example.com/comments/12"
      }
    }
  ]
}
```

### 4. 컬렉션 요청 (필터링 + 정렬 + 페이지네이션)

**요청:**
```http request
GET /articles?filter[author]=9&sort=-created&page[number]=1&page[size]=2 HTTP/1.1
Host: example.com
Accept: application/vnd.api+json
```

**응답:**
```http response
HTTP/1.1 200 OK
Content-Type: application/vnd.api+json

{
  "data": [
    {
      "type": "articles",
      "id": "1",
      "attributes": {
        "title": "JSON:API 소개",
        "content": "JSON:API는 API 응답 형식을 표준화합니다...",
        "created": "2025-02-10T15:30:00Z"
      },
      "relationships": {
        "author": {
          "data": { "type": "people", "id": "9" }
        }
      }
    },
    {
      "type": "articles",
      "id": "3",
      "attributes": {
        "title": "JSON:API 모범 사례",
        "content": "효과적인 JSON:API 설계를 위한 팁...",
        "created": "2025-01-25T09:15:00Z"
      },
      "relationships": {
        "author": {
          "data": { "type": "people", "id": "9" }
        }
      }
    }
  ],
  "links": {
    "self": "https://example.com/articles?filter[author]=9&sort=-created&page[number]=1&page[size]=2",
    "first": "https://example.com/articles?filter[author]=9&sort=-created&page[number]=1&page[size]=2",
    "next": "https://example.com/articles?filter[author]=9&sort=-created&page[number]=2&page[size]=2",
    "last": "https://example.com/articles?filter[author]=9&sort=-created&page[number]=3&page[size]=2"
  },
  "meta": {
    "totalPages": 3,
    "totalItems": 5
  }
}
```

### 5. POST 요청 예제 (새 리소스 생성)

**일반 REST API와 다른 점:** 
    - 일반적인 REST API에서는 POST 응답으로 Location 헤더만 반환하는 경우가 많지만,   
    JSON에서는 Location 헤더와 함께 생성된 리소스의 전체 표현을 응답 본문에 포함시켜 클라이언트가 추가 요청 없이 바로 리소스를 활용할 수 있도록 합니다.
        - `self 멤버`의 값은 `Location`과 일치해야 합니다.
    - **리소스 표현:** 응답 본문에는 생성된 리소스의 표현(일반적으로 전체 데이터)을 포함해야 합니다.
        - 후속 GET 요청 줄이는 것을 선호하기 때문에
    
**요청:**
```http request
POST /articles HTTP/1.1
Host: example.com
Content-Type: application/vnd.api+json
Accept: application/vnd.api+json

{
  "data": {
    "type": "articles",
    "attributes": {
      "title": "JSON:API와 GraphQL 비교",
      "content": "두 API 설계 방식의 장단점 분석..."
    },
    "relationships": {
      "author": {
        "data": { "type": "people", "id": "9" }
      }
    }
  }
}
```

**응답:**
```http response
HTTP/1.1 201 Created
Content-Type: application/vnd.api+json
Location: https://example.com/articles/12

{
  "data": {
    "type": "articles",
    "id": "12",
    "attributes": {
      "title": "JSON:API와 GraphQL 비교",
      "content": "두 API 설계 방식의 장단점 분석...",
      "created": "2025-03-22T10:30:45Z",
      "updated": "2025-03-22T10:30:45Z"
    },
    "relationships": {
      "author": {
        "data": { "type": "people", "id": "9" }
      }
    },
    "links": {
      "self": "https://example.com/articles/12"
    }
  }
}
```

### 6. PATCH 요청 예제 (리소스 업데이트)

`JSON:API`에서는 리소스 업데이트 시에 `PATCH`를 사용합니다.
- `PATCH`는 부분 업데이트이므로, `attributes`에 포함되지 않는 속성(누락된 값)은 현재 값인 것처럼 해석합니다.
- 서버는 누락된 속성을 null 값으로 해석해서는 안 됩니다.
   
리소스 업데이트는 3가지 타입이 있습니다.
- **속성 업데이트:** `attributes`에 있는 값을 업데이트합니다.
- **관계 업데이트:** 리소스 자체가 아닌 리소스 간의 관계만 업데이트합니다.(`data`)
- **벌크 업데이트:** 단일 요청으로 여러 작업을 배열로 보낼 수 있습니다. 해당 작업들은 순서대로 처리되고 모두 완전히 성공 함께 실패해야 합니다.
  - 현재 예시에서는 복잡하여 다루고 있지 않습니다.
  - **참조:** https://jsonapi.org/ext/atomic

**속성 업데이트**

**요청:**
```http request
PATCH /articles/12 HTTP/1.1
Host: example.com
Content-Type: application/vnd.api+json
Accept: application/vnd.api+json

{
  "data": {
    "type": "articles",
    "id": "12",
    "attributes": {
      "title": "JSON:API와 GraphQL 심층 비교"
    }
  }
}
```

**응답:**
```http response
HTTP/1.1 200 OK
Content-Type: application/vnd.api+json

{
  "data": {
    "type": "articles",
    "id": "12",
    "attributes": {
      "title": "JSON:API와 GraphQL 심층 비교",
      "content": "두 API 설계 방식의 장단점 분석...",
      "created": "2025-03-22T10:30:45Z",
      "updated": "2025-03-22T11:15:32Z"
    },
    "relationships": {
      "author": {
        "data": { "type": "people", "id": "9" }
      }
    },
    "links": {
      "self": "https://example.com/articles/12"
    }
  }
}
```

**관계 업데이트**

```http request
PATCH /articles/12 HTTP/1.1
Host: example.com
Content-Type: application/vnd.api+json
Accept: application/vnd.api+json

{
  "data": {
    "type": "articles",
    "id": "12",
    "relationships": {
      "author": {
        "data": { "type": "people", "id": "15" }
      },
      "tags": {
        "data": [
          { "type": "tags", "id": "5" },
          { "type": "tags", "id": "8" },
          { "type": "tags", "id": "12" }
        ]
      },
      "category": {
        "data": { "type": "categories", "id": "4" }
      },
      "reviewers": {
        "data": [
          { "type": "people", "id": "3" },
          { "type": "people", "id": "7" }
        ]
      }
    }
  }
}
```

```http response
HTTP/1.1 200 OK
Content-Type: application/vnd.api+json

{
  "data": {
    "type": "articles",
    "id": "12",
    "attributes": {
      "title": "JSON:API와 GraphQL 심층 비교",
      "content": "두 API 설계 방식의 장단점 분석...",
      "created": "2025-03-22T10:30:45Z",
      "updated": "2025-03-22T16:05:33Z"
    },
    "relationships": {
      "author": {
        "data": { "type": "people", "id": "15" },
        "links": {
          "self": "https://example.com/articles/12/relationships/author",
          "related": "https://example.com/articles/12/author"
        }
      },
      "tags": {
        "data": [
          { "type": "tags", "id": "5" },
          { "type": "tags", "id": "8" },
          { "type": "tags", "id": "12" }
        ],
        "links": {
          "self": "https://example.com/articles/12/relationships/tags",
          "related": "https://example.com/articles/12/tags"
        }
      },
      "category": {
        "data": { "type": "categories", "id": "4" },
        "links": {
          "self": "https://example.com/articles/12/relationships/category",
          "related": "https://example.com/articles/12/category"
        }
      },
      "reviewers": {
        "data": [
          { "type": "people", "id": "3" },
          { "type": "people", "id": "7" }
        ],
        "links": {
          "self": "https://example.com/articles/12/relationships/reviewers",
          "related": "https://example.com/articles/12/reviewers"
        }
      }
    },
    "links": {
      "self": "https://example.com/articles/12"
    }
  }
}
```

`relationships` 객체 내의 `links`에는 두 가지 표준 링크가 포함됩니다:   
- `self 링크 ("self": "https://example.com/articles/12/relationships/author")`: 이 링크는 관계 자체를 조작하기 위한 엔드포인트를 가리킵니다.
- `related 링크 ("related": "https://example.com/articles/12/author")`: 이 링크는 관련 리소스 자체에 접근하기 위한 엔드포인트입니다.


## 주요 구조 요소

- **data:** 요청의 주요 리소스를 포함하는 객체 또는 배열
  - **type:** 리소스의 유형 (예: articles, users, comments)
  - **id:** 리소스의 고유 식별자
  - **attributes:** 리소스의 데이터를 포함하는 객체
  - **relationships:** 다른 리소스와의 관계를 정의하는 객체
  - **links:** 리소스와 관련된 링크를 포함하는 객체
- **included:** 관련 리소스를 포함하는 배열 (복합 문서의 일부)
- **meta:** API 응답에 대한 메타데이터 정보
- **links:** 페이지네이션, 관련 리소스 등 다양한 링크 정보
- **errors:** 에러 발생 시 제공되는 오류 정보 배열 (위 예시에는 포함되지 않음)

## JSON:API를 선택할 때의 이점

- **표준화된 구조 및 문서**
  - 모든 JSON 구현체는 동일한 규칙을 따르므로 개발자가 새로운 API를 빠르게 이해할 수 있습니다.
  - 이미 관리중인 있는 문서가 있습니다.
- **네트워크 효율성: 성능 최적화**
  - **페이로드 최소화 기술:** 클라이언트가 필요한 필드만 요청할 수 있어 불필요한 데이터 전송을 방지합니다.
  - **HATEOAS 대비 효율성:** 모든 가능한 링크를 제공하는 대신 필요한 관계 정보만 포함하여 응답 크기를 최적화합니다.
  - 복합 문서를 통해 여러 연관 리소스를 단일 요청으로 가져올 수 있어 네트워크 왕복 시간을 절감합니다.
- **HATEOAS 대비 실용성**
  - 완전한 HATEOAS 구현 복잡성 없이도 하이퍼미디어 개념의 이점을 제공합니다.
  - 필요한 링크만 포함시켜 응답 크기와 복잡성을 줄이면서도 리소스 탐색 기능을 유지합니다.
  - 클라이언트 개발자가 직관적으로 이해하고 사용할 수 있는 간결한 구조를 제공합니다.
- **실용적 접근**
  - **계층적 링크 구조:** 리소스와 관련된 링크를 명확하게 구조화하여 클라이언트의 탐색을 용이하게 합니다.
  - **확장 가능한 메타데이터:** 응답에 페이지네이션, 캐싱 정보 등 유용한 메타데이터를 포함할 수 있습니다.
  - **클라이언트 자유도:** 표준을 따르면서도 클라이언트가 원하는 방식으로 데이터를 요청하고 처리할 수 있는 유연성을 제공합니다.

## 정리

`JSON:API`는 실용적인 측면에서 `HATEOAS`의 복잡성을 줄인 대안으로, 리소스 간 관계가 중요한 애플리케이션에 적합합니다.   
표준화된 형식을 통해 클라이언트와 서버 간의 통신을 명확하게 정의하고, 개발자들이 API를 더 쉽게 이해하고 사용할 수 있도록 돕습니다.

## 참조

- [JSON API: Explained in 4 minutes (+ EXAMPLES) - JSON:API 예제 빠르게 살펴보기](https://www.youtube.com/watch?v=N-4prIh7t38)
- [https://medium.com/@niranjan.cs/what-is-json-api-3b824fba2788 - JSON:API 사양에 대한 간략한 게시물](https://medium.com/@niranjan.cs/what-is-json-api-3b824fba2788)
- [spring-hateoas-jsonapi - 라이브러리 github](https://github.com/toedter/spring-hateoas-jsonapi)
- [JSON:API 공식 문서](https://jsonapi.org/format/)
