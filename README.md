# Приложение "Облачное хранилище"

## Для подключения к Front необходимо выполнить следующие инструкции:

### Описание и запуск FRONT

1. Установить nodejs (версия 14.x.x) на компьютер следуя инструкции: https://nodejs.org/ru/download/
2. Скачать [FRONT](https://github.com/netology-code/jd-homeworks/blob/master/diploma/netology-diplom-frontend) (
   JavaScript)
3. Перейти в папку FRONT приложения и все команды для запуска выполнять из нее.
4. Следуя описанию README.md FRONT проекта запустить nodejs приложение (npm install)
5. Можно задать url для вызова своего backend сервиса:
    - В файле .env FRONT (находится в корне проекта) приложения нужно изменить url до backend, например:
      VUE_APP_BASE_URL=http://localhost:8080
    - Пересобрать и запустить FRONT снова: npm run build
    - Измененный url сохранится для следующих запусков.
6. По умолчанию FRONT запускается на порту 8080 и доступен по url в браузере http://localhost:8080

### _Серверная часть запускается на порту 8090;_

## Запуск приложения

1. Для авторизации в приложении необходимо ввести login и password:
    1. login - "user@mail.ru", password - "qwerty";

2. После авторизации вы переходите на страницу самого приложения

3. Что можно делать в приложение:
    1. Добавление файла или фалов
    2. Скачивание файлов из хранилища
    3. Удаление файлов
    4. Вывод всех ваших загруженных файлов

###### Файл логов находится по следующему пути: logs/log_file.log 