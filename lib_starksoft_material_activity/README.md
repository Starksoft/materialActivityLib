Включает все необходимые библиотеки:

SlidingDrawer
Floating Action Button
PreferenceFragment
AppCompat v7

Описание:
Закрывает ActionMode и боковое меню при вызове onBackPressed()
Устанавливается с минимумом проблем и имеет все что нужно для работы

Инструкция:
1) Необходимо переопределять onBackPressed(), т.к. либа не вызывает super метод при выполнении onBackPressed(), это нужно для реализации стека фрагментов.
2) Для смены бекграунда у выдвижного меню нужно переопределить drawer_list_selector.xml и добавить его в папку res/drawable