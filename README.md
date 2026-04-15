# Álcool ou Gasolina

Aplicativo Android em Jetpack Compose para comparar preços de combustíveis e manter um cadastro local de postos.

Repositório: `https://github.com/jotapedz/alcool-gasolina`

## Funcionalidades

- Salva e restaura o estado do switch entre Álcool e Gasolina com `SharedPreferences`.
- Permite cadastrar, editar e excluir postos com valores de álcool e gasolina.
- Salva a data da informação e a localização atual do usuário junto com cada posto.
- Exibe lista de postos e tela de detalhes com opção de abrir a localização no mapa.
- Suporta internacionalização em português e inglês.

## Requisitos Atendidos

- Salvar e restaurar o estado do switch.
- CRUD de valores de combustível com `SharedPreferences` e JSON.
- Lista de postos com nome, preços e acesso ao detalhe.
- Tela de detalhe com edição, exclusão, data da informação e localização.
- Solicitação de permissão de localização e abertura do mapa por `Intent`.
- Internacionalização em português e inglês.

## Evidências
Tela Inicial

<img width="243" height="530" alt="image" src="https://github.com/user-attachments/assets/0d5dfcaa-23a7-4940-a110-402edfbe8312" />

Dados do posto e calculo da porcentagem

<img width="238" height="443" alt="image" src="https://github.com/user-attachments/assets/f746e098-5c09-491e-8041-793eccc6a1f1" />

Tela de calculo de gasolina/alcool

<img width="250" height="374" alt="image" src="https://github.com/user-attachments/assets/128eded3-3233-46ff-843c-4d98a8580fe7" />

Tela de posto salvo com sucesso

<img width="235" height="465" alt="image" src="https://github.com/user-attachments/assets/c2986c4e-33e4-4fec-9627-8152aab6fa5c" />

Tela de postos salvos

<img width="245" height="529" alt="image" src="https://github.com/user-attachments/assets/4d83761f-58f5-4d53-9835-9f3acdfa5597" />

Tela de detalhes do posto

<img width="241" height="530" alt="image" src="https://github.com/user-attachments/assets/bd42d131-50ce-49da-884c-810f1d178097" />

Após clicar em abrir mapa

<img width="244" height="532" alt="image" src="https://github.com/user-attachments/assets/4332de81-cf19-4222-858e-0de01310962d" />




### Sugestão de telas

- Tela inicial.
- Formulário com switch salvo/restaurado.
- Lista de postos cadastrados.
- Tela de detalhe com data, localização e botão de mapa.
