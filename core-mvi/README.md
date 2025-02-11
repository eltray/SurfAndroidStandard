[Главная страница репозитория](../docs/main.md)

[TOC]

# Core MVI
Является развитием идей [core-mvp](../core-mvp/) и [core-mvp-binding](../core-mvp-binding). 
Так же, этот подход во многом черпал вдохновение из [Redux](https://redux.js.org/), [Flux](https://ru.wikipedia.org/wiki/Flux-%D0%B0%D1%80%D1%85%D0%B8%D1%82%D0%B5%D0%BA%D1%82%D1%83%D1%80%D0%B0) и  [MVI Android](http://hannesdorfmann.com/android/model-view-intent).  

**`Внимание!` Модуль находится в стадии активной разработки!**

**`Внимание!` Ответственность за использование модуля в production полностью ложится на вас.**

## Общее описание
Подход заключается в полном абстрагировании сущностей UI-слоя друг от друга, 
и в выстраивании непрямых связей между ними на основе отсылки событий. 
Таким образом, получается решить проблему излишней запутанности связей между элементами 
и наладить полностью однонаправленный поток данных.

## Ответственности классов 
##### Event
Любое событие, обрабатываемое на UI-слое приложения. Пользовательский ввод, открытие экрана, загрузка данных, событие изменения сущности интерактора, и так далее. 
Удобнее всего эвенты одного экрана располагать в sealed class для ограничения их количества, и удобства навигации.

Реализации:

* [Event][ev]
##### EventHub
Шина событий. Все события экрана проходят через нее. Является типизированной множеству событий экрана, задаваемому как
Kotlin-sealed-class. 

Благодаря этому, достигается явное ограничение множества событий (все события расположены в одном файле), отсекается возможность пропуска EventHub'ом определенных событий (событие невозможно поместить в EventHub, если оно не соответствует его типу).

*Опционально*: У одного экрана может быть несколько шин, и они все могут быть типизированы по разным множествам событий.

Таким образом, можно разбить экран на независимые друг от друга куски логики отображения.

Реализации: 

* [EventHub][hub] - базовый типизированный хаб

* [RxEventHub][rxhub] - базовый хаб с поддержкой Rx

* [LifecycleEventHub][lchub] - базовый хаб, который автоматически реагирует на события жизненного цикла экрана.
##### StateHolder 
Класс, отвечающий за хранение состояния экрана, и его передачу View.

Реализации:

* Для [Reactor](#Reactor)-подхода базовая реализация не требуется, класс-холдер не обязательно должен наследоваться от какого-либо другого класса.

* [ReducerStateHolder][redholder] - StateHolder, имеющий единое состояние.

* [ReducerRxStateHolder][redrxholder] - StateHolder с единым реактивным состоянием.
##### Reactor
Класс, осуществляющий реакцию на события и преобразование текущего состояния экрана. Содержит единственный метод `react(holder, event)`, 
в котором обновляет поля у StateHolder в зависимости от пришедшего события. 

Реализации: 

* [Reactor][reactor]
##### Reducer 
Класс, выполняющий изменение state в зависимости от события. 
Содержит единственный метод `reduce(state, event): state`, в котором с помощью текущего состояния экрана и события происходит получение нового состояния. Является расширением Reactor, и для работы требуется совместно реализовать класс ReducerStateHolder.

Реализации: 

* [Reducer][reducer]
##### Middleware 
Промежуточный слой между UI и данными. Является аналогом `SideEffect` из Redux-терминологии. 

Middleware принимает в себя поток событий, трансформирует его произвольным образом, и направляет поток новых событий в `EvetHub`.

В терминологии `core-mvp`, является аналогом презентера, который, например, в качестве реакции на события нажатия на кнопку `reloadBtn`, перезагружает данные, однако имеет более декларативный синтаксис: 

`Observable<Event.Reload>` -> `Observable<Event.LoadData<T>>`

Чаще всего используется для получения данных из сервисного слоя, реакции на события ЖЦ, открытия экранов и подобных действий.

Может использоваться для комбинации двух состояний. Так как вызов метода `Middleware.transform` выполняется **после** выполнения `react` у `Reactor`/`Reducer`, данные, внутри `transform` всегда будут актуальными. 
Например, у кнопки , необходимо проставить isEnabled в зависимости от валидации некоторых полей. Поля изменяются с помощью событий `Event.FieldChanged`. Таким образом, при получении этого события в методе `transform`, `Reactor` уже отреагирует на событие и изменит модель, и мы сможем без проблем задействовать обновленную модель в Middleware, провести валидацию там, и вывести в выходящий поток `Event.IsDataValid(boolean)`

Реализации: 

* [Middleware][mw] - базовый типизированный Middleware

* [RxMiddleware][rxmw] - базовый Middleware с поддержкой Rx 
##### Binder 
Сущность, связывающая все остальные сущности вместе и осуществляющая подписку на шину сообщений.
У одного экрана может быть только один Binder.
Содержит методы для связи всех указанных выше сущностей для экранов с необходимостью отображения эвентов на UI, 
либо со связью только логики ([EventHub][hub], [Middleware][mw]), когда отображение не требуется (например, SplashScreen).

Реализации: 

* [RxBinder][rxbnd]

Схематично, поток данных между всеми сущностями, описанными выше, выглядит следующим образом: 

![Data Flow Diagram]( https://i.imgur.com/iFzYkOT.jpg )

Если попытаться представить поток данных линейно (от действия пользователя к загрузке данных из сети и отображению на экране данных, они будут выглядеть так: 

![Linear Data Flow]( https://i.imgur.com/rkjJmbq.jpg )

## Основные преимущества
Любые действие пользователя, вызовы методов жизненного цикла, получение данных с сервисного слоя, и отправка данных на UI рассматриваются как единая сущность: `событие`.
Все события, совершаемые на всех стадиях жизни экрана, проходят через единую шину: `хаб`. 
Таким образом, достигается полная абстракция классов внутри экрана, и устранение связей между ними. 

К тому же, благодаря единой точке входа и говорящему именованию, 
подход открывает возможность к очень легкому и действенному логированию всего, 
что в данный момент происходит на UI-слое приложения.

Важную роль играет разделение ответственностей между классами. 
Если в каноничном MVP Presenter отвечал и за управление подписками, и за хранение, и за трансформацию данных, 
в данном подходе было решено разделить его на независимые части. 

Так же, важную роль играет сохранение плюсов подхода реактивных биндингов: [core-mvp-binding](../core-mvp-binding). 
Модель `View` в каноничном виде MVI всегда отражает полное состояние экрана, и при любом изменении модели требуется полная перерисовка экрана. Этот подход реализован с помощью 
[State Reducer Pattern](https://medium.com/@ivanmontiel/discovering-the-state-reducer-pattern-3f324bb1a4c4). 
Однако из-за перерисовки экрана при получении каждого нового события, может значительно страдать производительность. 
Для того, чтобы этого избежать, вместо хранения в модели всех данных в простом виде, задействуются сущности [State][state] и [Command][cmd] из вышеописанного модуля: [core-mvp-binding](../core-mvp-binding). Благодаря им, `View` может вместо подписки на изменение экрана целиком, подписаться на изменение значений всех переменных, и перерисовыватьтолько небольшие части экрана, зависящие от этих переменных. Этот подход получил название `State Reactor`.
## Реализация на проектах
Базовые реализации классов вынесены из модуля для поддержания гибкости. Вы можете найти примеры реализаций в модуле [core-mvi-sample](../core-mvi-sample), в папке [ui/base](../core-mvi-sample/src/main/java/ru/surfstudio/android/core/mvi/sample/ui/base)

----

#### Вспомогательные сущности
Для упрощения загрузки данных из сервисного слоя была выделена сущность Loadable. 
Подход заключается в заключении `Observable<T>` в сущность Loadable<T>, которая содержит три состояния: 

* Loading - данные загружаются из сервисного слоя

* Data - данные пришли с сервисного слоя

* Error - ошибка загрузки данных

Основные сущности этого подхода: 

* [Loading][loading] - интерфейс-обертка над состоянием загрузки данных (аналогично LoadStateInterface). 
Необходим для удобного отображения состояния загрузки на ui

* [LoadType][ldtype] - реализация Loadable в виде sealed-класса

* [LoadableData][lddata] - состояние загрузки данных в удобной для отображения на UI форме

* [LoadableEvent][ldev] - событие загрузки данных

* [LoadableState][ldst] - State с удобным интерфейсом подписки на нужные статусы загрузки данных
# Подключение
Для подключения данного модуля из [Artifactory Surf](http://artifactory.surfstudio.ru)
необходимо, чтобы корневой `build.gradle` файл проекта был сконфигурирован так,
как описано [здесь](https://bitbucket.org/surfstudio/android-standard/overview).

Для подключения модуля через Gradle:
```
    implementation "ru.surfstudio.android:core-mvi:X.X.X"
```

[ev]: src/main/java/ru/surfstudio/android/core/mvi/event/Event.kt
[hub]: src/main/java/ru/surfstudio/android/core/mvi/event/hub/EventHub.kt
[rxhub]: src/main/java/ru/surfstudio/android/core/mvi/event/hub/RxEventHub.kt
[lchub]: src/main/java/ru/surfstudio/android/core/mvi/event/hub/lifecycle/LifecycleEventHub.kt
[mw]: src/main/java/ru/surfstudio/android/core/mvi/ui/middleware/Middleware.kt
[rxmw]: src/main/java/ru/surfstudio/android/core/mvi/ui/middleware/RxMiddleware.kt
[reactor]: src/main/java/ru/surfstudio/android/core/mvi/ui/reactor/Reactor.kt
[reducer]: src/main/java/ru/surfstudio/android/core/mvi/ui/reducer/Reducer.kt
[redholder]: src/main/java/ru/surfstudio/android/core/mvi/ui/holder/ReducerStateHolder.kt
[redrxholder]: src/main/java/ru/surfstudio/android/core/mvi/ui/holder/ReducerRxStateHolder.kt
[rxbnd]: src/main/java/ru/surfstudio/android/core/mvi/ui/binder/RxBinder.kt 

[loading]: ../core-mvp-binding/src/main/java/ru/surfstudio/android/core/mvp/rx/loadable/data/Loading.kt
[lddata]: ../core-mvp-binding/src/main/java/ru/surfstudio/android/core/mvp/rx/loadable/data/LoadableData.kt
[ldev]: src/main/java/ru/surfstudio/android/core/mvi/event/LoadableEvent.kt
[ldtype]: ../core-mvp-binding/src/main/java/ru/surfstudio/android/core/mvp/rx/loadable/event/LoadType.kt 
[ldst]: ../core-mvp-binding/src/main/java/ru/surfstudio/android/core/mvp/rx/loadable/state/LoadableState.kt

[state]: ../core-mvp-binding/src/main/java/ru/surfstudio/android/core/mvp/rx/domain/mvp/State.kt
[cmd]: ../core-mvp-binding/src/main/java/ru/surfstudio/android/core/mvp/rx/domain/mvp/Command.kt