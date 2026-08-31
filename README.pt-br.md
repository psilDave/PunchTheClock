# PunchTheClock ⏰

Um aplicativo moderno para controle de ponto e registro de horários, desenvolvido para Android e Wear OS.

## 🚀 O Projeto

O **PunchTheClock** foi desenvolvido para facilitar o registro de jornada de trabalho, oferecendo uma experiência integrada entre o smartphone e o relógio (smartwatch).

### Arquitetura e Módulos

O projeto utiliza uma arquitetura multi-módulos para garantir a reutilização de código e separação de responsabilidades:

-   **`:mobile`**: Aplicativo Android principal com interface em Jetpack Compose.
-   **`:wear`**: Aplicativo específico para Wear OS, permitindo registros rápidos diretamente do pulso.
-   **`:shared`**: Módulo de biblioteca contendo lógica de negócio, modelos de dados e banco de dados compartilhados entre as plataformas.

## 🛠 Tecnologias Utilizadas

-   **Linguagem**: [Kotlin](https://kotlinlang.org/)
-   **Interface UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Mobile & Wear)
-   **Injeção de Dependência**: [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
-   **Banco de Dados**: [Room](https://developer.android.com/training/data-storage/room)
-   **CI/CD**: GitHub Actions (Build e Testes automatizados)

---
**🌐 Versão em Inglês:** [README.md](./README.md)