# REST Server for Hardware Store

A simple Java REST server that manages hardware store inventory through JSON files.

## Overview

This project implements a RESTful API server that handles hardware store items (Ferramenta) stored as JSON files. Each item has an ID, a quantity (N), and a boolean flag indicating whether it's used or new.

## Features

- **CRUD Operations**: Supports Create, Read, Update, and Delete operations
- **Version Management**: Uses GSON's versioning feature to manage different API versions
- **File-based Storage**: All data is stored in JSON files within a directory structure

## API Endpoints

All endpoints use the base path `/index`.

### GET
- List all available resources: `/index/[path]`
- Get specific item: `/index/[path]?type=[filename]&version=[version]`

### POST
- Create new item: `/index/[path]?type=[filename]&id=[id]&N=[quantity]&Usato=[true|false]&version=[version]`

### PUT
- Update item: `/index/[path]?type=[filename]&id=[new_id]&N=[new_quantity]&version=[version]`

### DELETE
- Delete item: `/index/[path]?type=[filename]`

## Data Structure

Each hardware item (Ferramenta) contains:
- `id`: String identifier
- `N`: Integer quantity
- `Usato`: Boolean flag (available in version 1.1+)

## Running the Server

```
java RESTHttpServer -port [port_number]
```

Default port is 3000 if not specified.

## Directory Structure

The server serves files from the `./Root` directory, which contains subdirectories for different types of hardware items:

```
Root/
├── Bulloni/
│   ├── Ferro/
│   │   ├── v15x3.json
│   │   ├── v15x4.json
│   │   └── v15x7.json
│   └── Legno/
│       └── v85x5.json
└── Viti/
    └── v15x9.json
```

## Dependencies

- Java HTTP Server
- Google GSON for JSON parsing
