openapi: 3.0.0
info:
  description: "Вступительное задание в Осеннюю Школу Бэкенд Разработки Яндекса 2022"
  title: Yet Another Disk Open API
  version: "1.0"
paths:
  /imports:
    post:
      tags:
        - Базовые задачи
      description: |
        Импортирует элементы файловой системы. Элементы импортированные повторно обновляют текущие.
        Изменение типа элемента с папки на файл и с файла на папку не допускается.
        Порядок элементов в запросе является произвольным.

          - id каждого элемента является уникальным среди остальных элементов
          - поле id не может быть равно null
          - родителем элемента может быть только папка
          - принадлежность к папке определяется полем parentId
          - элементы могут не иметь родителя (при обновлении parentId на null элемент остается без родителя)
          - поле url при импорте папки всегда должно быть равно null
          - размер поля url при импорте файла всегда должен быть меньше либо равным 255
          - поле size при импорте папки всегда должно быть равно null
          - поле size для файлов всегда должно быть больше 0
          - при обновлении элемента обновленными считаются **все** их параметры
          - при обновлении параметров элемента обязательно обновляется поле **date** в соответствии с временем обновления
          - в одном запросе не может быть двух элементов с одинаковым id
          - дата обрабатывается согласно ISO 8601 (такой придерживается OpenAPI). Если дата не удовлетворяет данному формату, ответом будет код 400.

        Гарантируется, что во входных данных нет циклических зависимостей и поле updateDate монотонно возрастает. Гарантируется, что при проверке передаваемое время кратно секундам.
      requestBody:
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/SystemItemImportRequest"
      responses:
        "200":
          description: Вставка или обновление прошли успешно.
        "400":
          description: Невалидная схема документа или входные данные не верны.
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/Error"
              examples:
                response:
                  value: |-
                    {
                      "code": 400,
                      "message": "Validation Failed"
                    }
  /delete/{id}:
    delete:
      tags:
        - Базовые задачи
      description: |
        Удалить элемент по идентификатору. При удалении папки удаляются все дочерние элементы. Доступ к истории обновлений удаленного элемента невозможен.

        **Обратите, пожалуйста, внимание на этот обработчик. При его некорректной работе тестирование может быть невозможно.**
      parameters:
        - description: Идентификатор
          in: path
          name: id
          required: true
          schema:
            type: string
            format: id
          example: "элемент_1_1"
        - description: Дата и время запроса
          in: query
          name: date
          required: true
          schema:
            type: string
            format: date-time
          example: "2022-05-28T21:12:01.516Z"
      responses:
        "200":
          description: Удаление прошло успешно.
        "400":
          description: Невалидная схема документа или входные данные не верны.
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/Error"
              examples:
                response:
                  value: |-
                    {
                      "code": 400,
                      "message": "Validation Failed"
                    }
        "404":
          description: Элемент не найден.
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/Error"
              examples:
                response:
                  value: |-
                    {
                      "code": 404,
                      "message": "Item not found"
                    }
  /nodes/{id}:
    get:
      tags:
        - Базовые задачи
      description: |
        Получить информацию об элементе по идентификатору. При получении информации о папке также предоставляется информация о её дочерних элементах.

        - для пустой папки поле children равно пустому массиву, а для файла равно null
        - размер папки - это суммарный размер всех её элементов. Если папка не содержит элементов, то размер равен 0. При обновлении размера элемента, суммарный размер папки, которая содержит этот элемент, тоже обновляется.
      parameters:
        - description: Идентификатор элемента
          in: path
          name: id
          required: true
          schema:
            type: string
            format: id
          example: "элемент_1_1"
      responses:
        "200":
          description: Информация об элементе.
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/SystemItem"
        "400":
          description: Невалидная схема документа или входные данные не верны.
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/Error"
              examples:
                response:
                  value: |-
                    {
                      "code": 400,
                      "message": "Validation Failed"
                    }
        "404":
          description: Элемент не найден.
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/Error"
              examples:
                response:
                  value: |-
                    {
                      "code": 404,
                      "message": "Item not found"
                    }
  /updates:
    get:
      tags:
        - Дополнительные задачи
      description: |
        Получение списка **файлов**, которые были обновлены за последние 24 часа включительно [date - 24h, date] от времени переданном в запросе.
      parameters:
        - description: Дата и время запроса. Дата должна обрабатываться согласно ISO 8601 (такой придерживается OpenAPI). Если дата не удовлетворяет данному формату, необходимо отвечать 400
          in: query
          name: date
          required: true
          schema:
            type: string
            format: date-time
          example: "2022-05-28T21:12:01.000Z"
      responses:
        "200":
          description: Список элементов, которые были обновлены.
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/SystemItemHistoryResponse"
        "400":
          description: Невалидная схема документа или входные данные не верны.
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/Error"
              examples:
                response:
                  value: |-
                    {
                      "code": 400,
                      "message": "Validation Failed"
                    }
  /node/{id}/history:
    get:
      tags:
        - Дополнительные задачи
      description: |
        Получение истории обновлений по элементу за заданный полуинтервал [from, to). История по удаленным элементам недоступна.

        - размер папки - это суммарный размер всех её элементов
        - можно получить статистику за всё время.
      parameters:
        - in: path
          name: id
          schema:
            type: string
            format: id
          required: true
          description: id элемента для которого будет отображаться история
          example: "элемент_1_1"
        - in: query
          name: dateStart
          schema:
            type: string
            format: date-time
          required: false
          description: Дата и время начала интервала, для которого считается история. Дата должна обрабатываться согласно ISO 8601 (такой придерживается OpenAPI). Если дата не удовлетворяет данному формату, необходимо отвечать 400.
          example: "2022-05-28T21:12:01.000Z"
        - in: query
          name: dateEnd
          schema:
            type: string
            format: date-time
          required: false
          description: Дата и время конца интервала, для которого считается история. Дата должна обрабатываться согласно ISO 8601 (такой придерживается OpenAPI). Если дата не удовлетворяет данному формату, необходимо отвечать 400.
          example: "2022-05-28T21:12:01.000Z"
      responses:
        "200":
          description: История по элементу.
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/SystemItemHistoryResponse"
        "400":
          description: Некорректный формат запроса или некорректные даты интервала.
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/Error"
              examples:
                response:
                  value: |-
                    {
                      "code": 400,
                      "message": "Validation Failed"
                    }
        "404":
          description: Элемент не найден.
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/Error"
              examples:
                response:
                  value: |-
                    {
                      "code": 404,
                      "message": "Item not found"
                    }
components:
  schemas:
    SystemItemType:
      type: string
      description: Тип элемента - папка или файл
      enum:
        - FILE
        - FOLDER
    SystemItem:
      type: object
      required:
        - id
        - date
        - type
      properties:
        id:
          type: string
          nullable: false
          description: Уникальный идентфикатор
          example: "элемент_1_1"
        url:
          description: Ссылка на файл. Для папок поле равнно null.
          type: string
          nullable: true
        date:
          type: string
          format: date-time
          nullable: false
          description: Время последнего обновления элемента.
          example: "2022-05-28T21:12:01.000Z"
        parentId:
          type: string
          nullable: true
          description: id родительской папки
          example: "элемент_1_1"
        type:
          $ref: "#/components/schemas/SystemItemType"
        size:
          description: Целое число, для папки - это суммарный размер всех элеметов.
          type: integer
          nullable: true
          format: int64
        children:
          description: Список всех дочерних элементов. Для файлов поле равно null.
          type: array
          items:
            $ref: "#/components/schemas/SystemItem"
      example:
        id: "элемент_1_2"
        url: null
        type: FOLDER
        parentId: null
        date: "2022-05-28T21:12:01.000Z"
        size: 12
        children:
          - url: "/file/url1"
            id: "элемент_1_3"
            size: 4
            date: "2022-05-28T21:12:01.000Z"
            type: FILE
            parentId: "элемент_1_2"
          - type: FOLDER
            url: null
            id: "элемент_1_1"
            date: "2022-05-26T21:12:01.000Z"
            parentId: "элемент_1_2"
            size: 8
            children:
              - url: "/file/url2"
                id: "элемент_1_4"
                parentId: "элемент_1_1"
                date: "2022-05-26T21:12:01.000Z"
                size: 8
                type: FILE
    SystemItemImport:
      type: object
      required:
        - id
        - type
      properties:
        id:
          type: string
          nullable: false
          description: Уникальный идентфикатор
          example: "элемент_1_1"
        url:
          description: Ссылка на файл. Для папок поле равнно null.
          type: string
          nullable: true
        parentId:
          type: string
          nullable: true
          example: "элемент_1_1"
          description: id родительской папки
        type:
          $ref: "#/components/schemas/SystemItemType"
        size:
          nullable: true
          description: Целое число, для папок поле должно содержать null.
          type: integer
          format: int64
      example:
        id: "элемент_1_4"
        url: "/file/url1"
        parentId: "элемент_1_1"
        size: 234
        type: FILE
    SystemItemImportRequest:
      type: object
      properties:
        items:
          type: array
          description: Импортируемые элементы
          nullable: false
          items:
            $ref: "#/components/schemas/SystemItemImport"
        updateDate:
          type: string
          nullable: false
          format: date-time
          example: "2022-05-28T21:12:01.000Z"
          description: Время обновления добавляемых элементов.
    SystemItemHistoryUnit:
      type: object
      required:
        - id
        - type
        - date
      properties:
        id:
          type: string
          nullable: false
          description: Уникальный идентфикатор
          example: "элемент_1_1"
        url:
          description: Ссылка на файл. Для папок поле равнно null.
          type: string
          nullable: true
        parentId:
          type: string
          nullable: true
          description: id родительской папки
          example: "элемент_1_1"
        type:
          $ref: "#/components/schemas/SystemItemType"
        size:
          description: Целое число, для папки - это суммарный размер всех её элементов.
          type: integer
          format: int64
          nullable: true
        date:
          type: string
          nullable: false
          format: date-time
          description: Время последнего обновления элемента.
      example:
        id: "элемент_1_4"
        url: "/file/url1"
        date: "2022-05-28T21:12:01.000Z"
        parentId: "элемент_1_1"
        size: 234
        type: FILE
    SystemItemHistoryResponse:
      type: object
      properties:
        items:
          description: История в произвольном порядке.
          type: array
          items:
            $ref: "#/components/schemas/SystemItemHistoryUnit"
    Error:
      required:
        - code
        - message
      properties:
        code:
          nullable: false
          type: integer
        message:
          nullable: false
          type: string
