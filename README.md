# КиноМир — приложение для оценки фильмов

**Десктопное приложение** на JavaFX + PostgreSQL для просмотра фильмов, оставления отзывов и управления личным профилем.

## Назначение
Приложение позволяет:
- Просматривать список фильмов с постерами
- Читать и оставлять отзывы
- Редактировать личный профиль (имя, дата рождения, статус, любимый жанр, аватарка)
- Искать фильмы в реальном времени

## Стек технологий
- **Java 24**
- **JavaFX 17**
- **PostgreSQL 16**
- **Maven**
- **JDBC**
- **FXML + CSS**

## Установка и запуск

1. Установите **PostgreSQL** и создайте базу данных `moviedb`
2. Выполните SQL-скрипт (создание таблиц, процедуры и триггера)
3. Настройте подключение в `DatabaseConfig.java`
4. Запустите класс `Starter.java`

## Использование
- При запуске открывается главное окно
- Для работы с отзывами и профилем необходимо авторизоваться
- Переключение между вкладками: Информация / Отзывы / Оценить / **Профиль**
- Поиск работает в реальном времени

## Скриншоты

<img width="370" height="221" alt="image" src="https://github.com/user-attachments/assets/acd761bf-a370-467f-88aa-bea60aeaecdd" />
<img width="377" height="231" alt="image" src="https://github.com/user-attachments/assets/c98b3fda-cac0-4c23-aa05-99e72a315770" />
<img width="479" height="195" alt="image" src="https://github.com/user-attachments/assets/1db7ae50-30ac-4eb5-be9c-793cf1c4a1d0" />
<img width="494" height="254" alt="image" src="https://github.com/user-attachments/assets/46dfa4dc-f4c4-45e5-89c6-c5be4b1d0785" />


