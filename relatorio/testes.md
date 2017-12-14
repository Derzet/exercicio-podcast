# Testes
Utilizando JUnit para teste classes unitários e o Espresso para de interfaces gráficas. 
Elaboramos alguns para testar

---
#Unitários, optamos em testar as classes Architecture Components 
para verificar o funcionamento do mesmo em nosso app.

```
Como teste unitário, nós acabamos fazendo apenas alguns testes simples sobre a classe podcast, sendo que o mais interessante deles acabou sendo feito por conta uma annotation de autoincrement utilizada por nós e que em algumas situações, essas funções de autoincrement podem causar erros graves que podem acabar passando despercebidos.

Também testamos a criação, inserção e acesso ao banco de dados. Um detalhe é que inicialmente, deveria ser feito como teste unitário, mas por conta da necessidade de receber um contexto no momento da criação do banco de dados, nós fomos impossibilitados de realizá-los dessa forma, portanto, tivemos que fazer esses testes utilizando o junit porém como testes instrumentais por terem acesso ao contexto da aplicação, permitindo assim que testassemos algumas funções do banco de dados que após a refatoração estava baseado no room recém lançado do android.
```

---
#GUI
----
```
Fizemos alguns testes simples de gui utilizando o espresso, sendo eles um teste para verificar se a listview inicial contendo os podcasts estava sendo exibida na activity principal do app. Outro teste verificava apenas se a listview era clicável e por fim um último teste que observava se o o podcast Ciência e Pseudociência na list view inicial na posição 0 e realizava um click no mesmo.
```