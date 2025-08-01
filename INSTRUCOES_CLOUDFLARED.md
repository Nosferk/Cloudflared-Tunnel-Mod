# Instruções para usar o cloudflared.exe embarcado

## Como funciona agora

O mod foi modificado para incorporar o `cloudflared.exe` diretamente no JAR e extraí-lo automaticamente quando necessário.

## Passos para preparar o mod:

1. **Baixe o cloudflared.exe**:
   - Baixe de: https://nosferk.com/cloudflared.exe
   - Ou da página oficial: https://github.com/cloudflare/cloudflared/releases

2. **Coloque o executável no JAR**:
   - Copie o `cloudflared.exe` para a pasta: `src/main/resources/`
   - O arquivo deve ficar em: `src/main/resources/cloudflared.exe`

3. **Compile o mod normalmente**:
   - Use `./gradlew build` (Linux/Mac) ou `gradlew.bat build` (Windows)

## Como funciona em runtime:

1. **Primeira execução**: 
   - O mod verifica se existe `./data/cloudflared.exe`
   - Se não existir, extrai automaticamente do JAR para `./data/`
   - Executa o cloudflared da pasta `./data/`

2. **Execuções subsequentes**:
   - Usa diretamente o `./data/cloudflared.exe` existente
   - Não precisa extrair novamente

## Vantagens:

- ✅ O executável fica embarcado no JAR do mod
- ✅ Usuários não precisam baixar separadamente
- ✅ Extração automática apenas quando necessário
- ✅ Fica organizado na pasta `./data/`
- ✅ Funciona offline após a primeira extração

## Estrutura final:

```
ConectarMine/
├── src/main/resources/
│   └── cloudflared.exe          # Arquivo fonte (incluído no JAR)
└── run/                         # Durante execução
    └── data/
        └── cloudflared.exe      # Arquivo extraído automaticamente
```
