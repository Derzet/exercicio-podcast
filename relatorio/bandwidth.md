# Bandwidth
#Análise
 Consta o uso da rede no inicio do download e uma descreção em seu termínio, utilizando o Android Profiler.
 

Durante Download : Uso de rede intenso (1,36MB para o Download)

Fim Download : Consumo diminuindo e com oscilação mais lenta até o download ser finalizado e o uso
da banda ser zerado.
---

# Boas práticas

A Requisição dos dados é armazenada previamente no banco de dados local, consequentemente 
com os dados local a exibição
funciona sem internet na segunda execução do aplicativo , 
evitando um Poll da rede para exibição dos dados.

----


