# Cloudflared Tunnel Mod


---

## Índice

- [O que é o Cloudflared Tunnel Mod?](#-o-que-é-o-cloudflared-tunnel-mod)
- [Como Funciona](#-como-funciona)
- [Instalação](#-instalação)
- [Configuração](#-configuração)
- [Como Usar](#-como-usar)
- [Requisitos](#-requisitos)
- [Resolução de Problemas](#-resolução-de-problemas)
- [Contribuição](#-contribuição)
- [Licença](#-licença)

---

## O que é o Cloudflared Tunnel Mod?

O **Cloudflared Tunnel Mod** é um mod para Minecraft Fabric que integra o **Cloudflare Tunnel** (cloudflared) ao seu cliente Minecraft. Ele permite que você crie túneis para conectar-se a servidores Minecraft sem precisar configurar port forwarding ou lidar com IPs dinâmicos.

### Onde veio a ideia?

Esta solução foi inspirada por discussões na comunidade sobre como usar túneis Cloudflare para jogos:

- **[Running a Minecraft Server](https://www.reddit.com/r/CloudFlare/comments/wdpe1p/cloudflare_tunnel_running_a_minecraft_server/)** - Discussão no Reddit sobre hospedagem de servidores Minecraft via Cloudflare Tunnel

- **[Is it possible to use a tunnel?](https://community.cloudflare.com/t/its-possible-to-use-a-tunnel-to-open-a-minecraft-server/619949)** - Thread oficial na comunidade Cloudflare explorando a viabilidade técnica

- **[Hosting through Cloudflare](https://www.reddit.com/r/CloudFlare/comments/1iw2fjt/hosting_a_minecraft_server_through_cloudflare/)** - Experiências da comunidade com hosting de servidores

O mod automatiza todo o processo manual discutido nessas threads, tornando a solução acessível a todos os jogadores.

## Como Funciona

### Funcionamento Técnico

1. **Inicialização Automática**: Quando você inicia o Minecraft com o mod instalado, ele automaticamente:
   - Extrai o executável `cloudflared.exe` do arquivo JAR para a pasta `./data/`
   - Inicia um túnel Cloudflare usando suas configurações
   - Conecta o túnel a uma porta local específica

2. **Gestão de Processos**: O mod gerencia todo o ciclo de vida do processo cloudflared:
   - Inicia o processo automaticamente
   - Monitora a saída e logs
   - Para o processo quando necessário
   - Reconecta automaticamente em caso de falhas

### Arquitetura de Rede

```
[Seu Minecraft] ←→ [Túnel Local] ←→ [Cloudflare Edge] ←→ [Internet] ←→ [Outros Jogadores]
     Porta Local        cloudflared       Rede Global            Acesso Público
```

## Por que Usar



## Instalação

### **Pré-requisitos**
- **Minecraft 1.21.8**
- **Fabric Loader 0.16.14+**
- **Fabric API**
- **Java 21+**

### **Passos de Instalação**

1. **Baixe os Requisitos**:
   - [Fabric](https://fabricmc.net/use/installer/)
   - [Fabric API](https://modrinth.com/mod/fabric-api)

2. **Baixe o Mod**:
   - Coloque o arquivo `.jar` na pasta `mods/`

## ⚙️ Configuração

### 📁 **Arquivo de Configuração**
O arquivo `config/cloudflared-tunnel.properties` é criado automaticamente:

```properties
# Usar porta aleatória (recomendado para evitar conflito com outros jogadores)
useRandomPort=true

# Porta fixa (se useRandomPort=false)
fixedPort=25565

# Hostname do túnel
hostname=play.exemple.com

# Adicionar automaticamente à lista de servidores
autoAddToServerList=true

# Nome do servidor na lista
serverName=Nosferk
```

### **Configuração In menu**
Acesse o menu de configurações através do mod menu para ajustar as configurações.

## Como Usar

### **Uso Básico**

1. **Inicie o Minecraft** com o mod instalado
2. **Aguarde a Inicialização**: O túnel será criado automaticamente
3. **Verifique os Logs**: Confirme se o túnel foi estabelecido com sucesso

### **Monitoramento**

- **Logs**: Verifique `logs/latest.log` para informações detalhadas
- **Processo**: O mod gerencia automaticamente o processo cloudflared

### **Recarregamento**

Se precisar alterar configurações:
1. Modifique o arquivo de configuração
2. Use a função reload na GUI do mod
3. O túnel será reiniciado automaticamente

## Requisitos

### **Sistema**
- **Windows**: Windows 10/11 (suporte nativo)
- **Linux**: Ubuntu 18.04+ / Debian 9+ / CentOS 7+
- **macOS**: macOS 10.15+

### **Minecraft**
- **Versão**: 1.21.8
- **Mod Loader**: Fabric 0.16.14+
- **APIs**: Fabric API
- **Java**: OpenJDK/Oracle JDK 21+

### **Rede**
- **Conexão**: Internet estável
- **Portas**: Acesso de saída à internet (HTTPS/80,443)
- **Firewall**: Permissão para cloudflared.exe

## Resolução de Problemas

### **Problemas Comuns**

**"cloudflared.exe não encontrado"**
- Certifique-se que o arquivo está incluído na pasta `./data/`
- Verifique permissões da pasta `./data/`

**"Erro ao iniciar túnel"**
- Verifique sua conexão com a internet
- Confirme que não há bloqueios de firewall
- Tente uma porta diferente

**"Não consegue conectar ao servidor"**
- Confirme que o túnel está ativo nos logs
- Verifique se a porta local está correta
- Teste conectividade local primeiro

### **Diagnóstico**

1. **Verifique Logs**: `logs/latest.log`
2. **Teste Conectividade**: Ping para cloudflare.com
3. **Verifique Processos**: Confirme se cloudflared está executando
4. **Teste Local**: Conecte-se localmente primeiro

### **Obtendo Ajuda**

- **Issues**: [GitHub Issues](https://github.com/Nosferk/Cloudflared-Tunnel-Mod/issues)
- **Documentação**: [Wiki do Projeto](https://nosferk.com/projects/cloudflared-tunnel)
- **Website**: [nosferk.com](https://nosferk.com/projects/cloudflared-tunnel)

## Contribuição

Contribuições são bem-vindas! Por favor:

1. **Fork** o repositório
2. **Crie** uma branch para sua feature
3. **Commit** suas mudanças
4. **Push** para a branch
5. **Abra** um Pull Request

### **Diretrizes**

- Siga as convenções de código existentes
- Adicione testes quando apropriado
- Documente mudanças significativas
- Use commits descritivos

## Licença

Este projeto está licenciado sob **Attribution-NonCommercial-NoDerivatives 4.0 International**.

---

## Autor

**Nosferk**
- Website: [nosferk.com](https://nosferk.com)
- Projeto: [Cloudflared Tunnel](https://nosferk.com/projects/cloudflared-tunnel)
- GitHub: [@Nosferk](https://github.com/Nosferk)
