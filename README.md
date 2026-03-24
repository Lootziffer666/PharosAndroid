# PHAROS Android
Chat-ID: CH-20260308-04

Modular Android Studio baseline for document analysis, project clustering, and an AnythingLLM-like LLM gateway setup.

It supports a hybrid model:
- mobile-light pipelines on the phone
- heavier pipelines on a PC-hosted local gateway

## Features

- **Document Scanner** – SAF-based recursive file scanning with SHA-256 change detection
- **AI Analysis** – Perplexity API integration for document analysis (topics, summaries, action items)
- **Project Clustering** – auto-derives projects from analysis results
- **Masterfile Generation** – writes Markdown project overviews into user folder
- **Room Database** – persistent storage with 5 entities
- **Encrypted Settings** – API keys stored via EncryptedSharedPreferences + Android Keystore
- **PDF Support** – PDFBox-Android for text extraction from PDFs
- **3 LLM Providers** – Perplexity, Ollama (local), Custom OpenAI gateway
- **Budget Management** – daily spend limit with "cheap-first" policy

## Architecture

```
com.flow.pharos/
├── core/model/         # Data models, Room entities, enums
├── core/storage/       # Room DB, DAOs, repositories, settings
├── core/llm/           # LlmGateway + AiApiProvider interfaces, JSON parser
├── feature/archive/    # Legacy archive screen
├── feature/relations/  # Legacy relations screen
├── feature/settings/   # Legacy settings screen
├── provider/perplexity/  # Perplexity API (ping + document analysis)
├── provider/ollama/      # Ollama local models
├── provider/customopenai/# Custom OpenAI gateway
└── app/                  # Main app, Hilt DI, screens, ViewModels, use cases
```

## Screens (5 tabs)

1. **Dashboard** – scan/analyze/update with progress + cancel
2. **Folders** – SAF folder picker
3. **Files** – file list with analysis status icons
4. **Projects** – auto-generated project overview
5. **Settings** – API key management, analysis mode

## Build

```bash
./gradlew assembleDebug
```

## Test

```bash
./gradlew test
```
