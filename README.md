# XOPlayerVsPlayerWeb
Игра крестики-нолики между 2мя пользователями через сервер с использованием Swing.
-Для запуска игры нужно вначале запустить класс Server, затем MainClient.
-К серверу могут подключаться бесконечное колличество пар игроков.
-Сервер рандомно выбирает какой из игроков будет ходить первым.
-В информационном поле пишется какой из игроков ходит.На время хода одного игрока, у второго блокируются все клетки.
-В конце игры, после выхода одного из игроков, второму посылается сообщение с предложением зайти еще раз
 на сервер для продолжения игры с другим соперником. После этого сообщения второго игрока отключает от сервера.
-Пока не обработан случай выхода одного из игроков в середине игры.
