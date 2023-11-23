# language: pt
Funcionalidade: Mensagem

  Cenario: Registrar Mensagem
    Quando registrar uma nova mensagem
    Entao a mensagem é registrada com sucesso
    E deve ser apresentada

  Cenario: Buscar Mensagem
    Dado que uma mensagem já foi publicada
    Quando efetuar a busca da mensagem
    Entao a mensagem é exibida com sucesso

  Cenario: Alterar Mensagem
    Dado que uma mensagem já foi publicada
    Quando efetuar requisição para alterar mensagem
    Entao a mensagem é atualizada com sucesso
    E deve ser apresentada

  Cenario: Remover Mensagem
    Dado que uma mensagem já foi publicada
    Quando requisitar a remoção da mensagem
    Entao a mensagem é removida com sucesso

  Cenario: Listar Mensagens
    Quando efetuar a listagem das mensagens
    Entao as mensagens serão exibidas