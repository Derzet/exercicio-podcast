# Memoria

#Análise
Utilizando Android Profiler, analisou estimou-se o 
consumo médio de memória: 

Activity Principal : 15,17MB , apresentando o menor consumo devido a ser um estado
inicial.

Detalhes do episodio: Uso de memória constante 18,52MB

Main Activity Atualizando List View: Utilização moderada da CPU porém ainda de forma rápida, uso de 24,21MB 

Durante Download: Memória alocada ainda crescendo no momento (56,46 MB) , percebe-se que o maior uso de memória
é nesta etapa.

Fim Download: Uso de memória que estava crescente estabiliza a 78,75MB quase que constante
Tocando o Podcast: Uso de memória sobe e permanece constante em 78.8 MB.


Constatou-se Memory Leak quando estava modificando o código, mas o Leak Canary não apresentou 
outro aviso. 

---



# Boas práticas
----

- No ciclo de vida da MainActivity fizemos uso de destruir o objeto manualmente , 
talvez isso tenha gerado a não detectação de memory leak no código.
