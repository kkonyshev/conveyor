conveyor
========

Имеется очередь элементов на обработку. Каждый элемент имеет собственный идентификатор (itemId) и принадлежит к некоторой группе (groupId). Внутри группы элементы должны обрабатываться строго последовательно, в порядке увеличения идентификаторов элементов. Элементы разных групп могут обрабатываться параллельно. Обработка элемента производится путем вызова некоторого метода с параметрами itemId и groupId, который печатает полученные идентификаторы элементов. Элементы в очередь добавляются асинхронно внешним процессом. После обработки элемент должен быть удален из очереди. 
Написать обработчик очереди, работающий в несколько потоков. Максимальное количество потоков ограничено, задается при старте обработчика и в общем случае меньше числа групп. Обеспечить равномерную обработку групп элементов: наличие в очереди групп с большим количеством элементов не должно приводить к длительным задержкам в обработке других групп.


1. Сборка:
	mvn clean install -DskipTests=true


2. Сменить текущую директорию:
	cd target


3. Запуск:
3.1 с параметрами по-умолчанию (2 потока, порт 8082)
	java -jar conveyor-0.2-SNAPSHOT-jar-with-dependencies.jar
3.2 описание параметров запуска
	java -jar conveyor-0.2-SNAPSHOT-jar-with-dependencies.jar -h

	
4. добавление элементов в очередь обработки
4.1 GET  localhost:8082 -- форма добавления (единичного элемента с заданными идентификаторами, пачки элементов заданного размера со случайными идентификаторами)
4.2 POST localhost:8082/create/ -- Добавление единичного элемента с заданными идентификаторами. Параметы запроса: идентификатор группы -- "groupId", идентификатор элемента -- "id"
4.3 POST localhost:8082/create/batch/ -- Добавление пачки элементов заданного размера. Параметры: "batchSize" -- количество добавляемых в очередь элементов (по-умолчанию: FrontImpl.DEFAULT_BATCH_CREATE_SIZE=5)

5. Отображение процсаа обработки:
5.1 вывод на консоль сообщений види "18:[1][37]/5", где 18 -- идентификатор потока, 1 -- идентификатор гуппы, 17 -- идентификатор элемента, 5 -- общее количество элементов обработанных потоком с начала запуска приложения

6. Журналирование
6.1 logs/SYSTEM.log -- общий протокол работы
6.2 logs/PROCESSING.log --журналирование операция потоков обработки
6.3 logs/WEB.log -- журналирование web-обращений
6.4 logs/DISPATCHER.log -- журналирование работы диспетчера
