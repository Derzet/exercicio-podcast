# ArchComponents
A implementação do componente Room iniciou-se utilizando como referência o 
guia https://developer.android.com/topic/libraries/architecture/guide.html de developer.

---

# Refatoração

Foram adicionadas e modificada as classes  para refatoração do projeto :
[+]Podcast: Classe básica para geração de tabelas usando para armazenamento de dados de 
um podcast na aplicação.
[+]PodcastDao : Classe para inserção, atualização e fazer queries em minha tabela Podcast.
[+]AppDatabase : Nesta classe utilizou-se o padrão 
singleton para o db garatindo uma referência única global
tornando-se o acesso mais fácil de gerenciar. Sendo este o acesso ao banco melhorar o desempenho.

[MODIFICADA] PodCastProvider, utilizando métodos gerados pela instância do AppDatabase.

--

# Análise

Em razão da modificação ser realizada no provider que funciona como uma interface
 é cabível que a refatoração tornou-se mais fácil a refatoração do banco de dados.





