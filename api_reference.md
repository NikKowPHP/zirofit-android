{
  "openapi": "3.0.3",
  "info": {
    "title": "zirofit-next Documentation",
    "description": "Automatically generated documentation based on available route handlers.",
    "version": "1.0.0",
    "contact": {
      "name": "API Support"
    }
  },
  "servers": [
    {
      "url": "http://localhost:3000",
      "description": "Local development server."
    }
  ],
  "paths": {
    "/api/trainers": {
      "get": {
        "summary": "GET /api/trainers",
        "description": "Search and list public trainers.",
        "tags": [
          "trainers"
        ],
        "parameters": [
          {
            "name": "page",
            "in": "query",
            "required": false,
            "schema": {
              "type": "number",
              "example": 0
            }
          },
          {
            "name": "pageSize",
            "in": "query",
            "required": false,
            "schema": {
              "type": "number",
              "example": 0
            }
          },
          {
            "name": "q",
            "in": "query",
            "required": false,
            "schema": {
              "type": "string"
            }
          },
          {
            "name": "location",
            "in": "query",
            "required": false,
            "schema": {
              "type": "string"
            }
          },
          {
            "name": "sortBy",
            "in": "query",
            "required": false,
            "schema": {
              "type": "string"
            }
          },
          {
            "name": "minRating",
            "in": "query",
            "required": false,
            "schema": {
              "type": "number",
              "example": 0
            }
          },
          {
            "name": "specialties",
            "in": "query",
            "required": false,
            "schema": {}
          },
          {
            "name": "trainingTypes",
            "in": "query",
            "required": false,
            "schema": {}
          }
        ],
        "responses": {
          "200": {
            "description": "Trainers retrieved successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "trainers": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "id": {
                            "type": "string"
                          },
                          "name": {
                            "type": "string"
                          },
                          "username": {
                            "type": "string",
                            "nullable": true
                          },
                          "profile": {
                            "type": "object",
                            "properties": {
                              "profilePhotoPath": {
                                "type": "string",
                                "nullable": true
                              },
                              "certifications": {
                                "type": "string",
                                "nullable": true
                              },
                              "averageRating": {
                                "type": "number",
                                "example": 0,
                                "nullable": true
                              }
                            },
                            "required": [
                              "profilePhotoPath",
                              "certifications",
                              "averageRating"
                            ],
                            "nullable": true
                          }
                        },
                        "required": [
                          "id",
                          "name",
                          "username",
                          "profile"
                        ]
                      }
                    },
                    "totalTrainers": {
                      "type": "number",
                      "example": 0
                    },
                    "currentPage": {
                      "type": "number",
                      "example": 0
                    },
                    "totalPages": {
                      "type": "number",
                      "example": 0
                    }
                  },
                  "required": [
                    "trainers",
                    "totalTrainers",
                    "currentPage",
                    "totalPages"
                  ]
                }
              }
            }
          }
        }
      }
    },
    "/api/openapi": {
      "get": {
        "summary": "GET /api/openapi",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "openapi"
        ],
        "responses": {
          "200": {
            "description": "Successful response."
          }
        }
      }
    },
    "/api/notifications": {
      "get": {
        "summary": "GET /api/notifications",
        "description": "List notifications for the authenticated user.",
        "tags": [
          "notifications"
        ],
        "responses": {
          "200": {
            "description": "Notifications retrieved successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "notifications": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "id": {
                            "type": "string"
                          },
                          "userId": {
                            "type": "string"
                          },
                          "message": {
                            "type": "string"
                          },
                          "type": {
                            "type": "string"
                          },
                          "readStatus": {
                            "type": "boolean"
                          },
                          "createdAt": {
                            "type": "string"
                          }
                        },
                        "required": [
                          "id",
                          "userId",
                          "message",
                          "type",
                          "readStatus",
                          "createdAt"
                        ]
                      }
                    }
                  },
                  "required": [
                    "notifications"
                  ]
                }
              }
            }
          }
        }
      }
    },
    "/api/exercises": {
      "get": {
        "summary": "GET /api/exercises",
        "description": "Search and list exercises.",
        "tags": [
          "exercises"
        ],
        "parameters": [
          {
            "name": "search",
            "in": "query",
            "required": false,
            "schema": {
              "type": "string"
            }
          },
          {
            "name": "page",
            "in": "query",
            "required": false,
            "schema": {
              "type": "number",
              "example": 0
            }
          },
          {
            "name": "limit",
            "in": "query",
            "required": false,
            "schema": {
              "type": "number",
              "example": 0
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Exercises retrieved successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "exercises": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "id": {
                            "type": "string"
                          },
                          "name": {
                            "type": "string"
                          },
                          "muscleGroup": {
                            "type": "string",
                            "nullable": true
                          },
                          "equipment": {
                            "type": "string",
                            "nullable": true
                          },
                          "category": {
                            "type": "string",
                            "nullable": true
                          }
                        },
                        "required": [
                          "id",
                          "name",
                          "muscleGroup",
                          "equipment",
                          "category"
                        ]
                      }
                    },
                    "total": {
                      "type": "number",
                      "example": 0
                    },
                    "page": {
                      "type": "number",
                      "example": 0
                    },
                    "pageSize": {
                      "type": "number",
                      "example": 0
                    },
                    "hasMore": {
                      "type": "boolean"
                    }
                  },
                  "required": [
                    "exercises",
                    "total",
                    "page",
                    "pageSize",
                    "hasMore"
                  ]
                }
              }
            }
          }
        }
      }
    },
    "/api/dashboard": {
      "get": {
        "summary": "GET /api/dashboard",
        "description": "Get mobile dashboard data for the authenticated trainer.",
        "tags": [
          "dashboard"
        ],
        "responses": {
          "200": {
            "description": "Mobile dashboard data retrieved successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "user": {},
                    "profileChecklist": {},
                    "upcomingSessions": {
                      "type": "array",
                      "items": {}
                    },
                    "activityFeed": {
                      "type": "array",
                      "items": {}
                    },
                    "businessPerformance": {},
                    "clientEngagement": {},
                    "servicePopularity": {},
                    "clients": {
                      "type": "array",
                      "items": {}
                    },
                    "programsAndTemplates": {}
                  },
                  "required": [
                    "user",
                    "profileChecklist",
                    "upcomingSessions",
                    "activityFeed",
                    "businessPerformance",
                    "clientEngagement",
                    "servicePopularity",
                    "clients",
                    "programsAndTemplates"
                  ]
                }
              }
            }
          },
          "403": {
            "description": "Forbidden."
          }
        }
      }
    },
    "/api/contact": {
      "post": {
        "summary": "POST /api/contact",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "contact"
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "name": {
                    "type": "string",
                    "minLength": 1
                  },
                  "email": {
                    "type": "string",
                    "format": "email"
                  },
                  "message": {
                    "type": "string",
                    "minLength": 10
                  },
                  "trainerEmail": {
                    "type": "string",
                    "format": "email"
                  },
                  "trainerName": {
                    "type": "string",
                    "minLength": 1
                  }
                },
                "required": [
                  "name",
                  "email",
                  "message",
                  "trainerEmail",
                  "trainerName"
                ]
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "200 response."
          },
          "400": {
            "description": "400 response."
          },
          "500": {
            "description": "500 response."
          }
        }
      }
    },
    "/api/clients": {
      "get": {
        "summary": "GET /api/clients",
        "description": "List all clients for the authenticated trainer.",
        "tags": [
          "clients"
        ],
        "responses": {
          "200": {
            "description": "Successfully retrieved client list.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "clients": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "id": {
                            "type": "string"
                          },
                          "name": {
                            "type": "string"
                          },
                          "email": {
                            "type": "string",
                            "nullable": true
                          },
                          "phone": {
                            "type": "string",
                            "nullable": true
                          },
                          "status": {
                            "type": "string",
                            "enum": [
                              "active",
                              "inactive",
                              "pending"
                            ]
                          },
                          "userId": {
                            "type": "string"
                          },
                          "lastWorkoutDate": {
                            "type": "string"
                          },
                          "engagementScore": {
                            "type": "string",
                            "enum": [
                              "high",
                              "medium",
                              "low"
                            ]
                          },
                          "avatarUrl": {
                            "type": "string"
                          }
                        },
                        "required": [
                          "id",
                          "name",
                          "status",
                          "engagementScore"
                        ]
                      }
                    },
                    "isPremium": {
                      "type": "boolean"
                    }
                  },
                  "required": [
                    "clients",
                    "isPremium"
                  ]
                }
              }
            }
          },
          "500": {
            "description": "Internal server error."
          }
        }
      },
      "post": {
        "summary": "POST /api/clients",
        "description": "Create a new client.",
        "tags": [
          "clients"
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "name": {
                    "type": "string",
                    "minLength": 1
                  },
                  "email": {},
                  "phone": {},
                  "status": {
                    "type": "string",
                    "enum": [
                      "active",
                      "inactive",
                      "pending"
                    ]
                  }
                },
                "required": [
                  "name",
                  "email",
                  "phone"
                ]
              }
            }
          }
        },
        "responses": {
          "201": {
            "description": "Successfully created client.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "client": {
                      "type": "object",
                      "properties": {
                        "id": {
                          "type": "string"
                        },
                        "name": {
                          "type": "string"
                        },
                        "email": {
                          "type": "string",
                          "nullable": true
                        },
                        "phone": {
                          "type": "string",
                          "nullable": true
                        },
                        "status": {
                          "type": "string",
                          "enum": [
                            "active",
                            "inactive",
                            "pending"
                          ]
                        },
                        "trainerId": {
                          "type": "string"
                        }
                      },
                      "required": [
                        "id",
                        "name",
                        "email",
                        "phone",
                        "status",
                        "trainerId"
                      ]
                    }
                  },
                  "required": [
                    "client"
                  ]
                }
              }
            }
          },
          "400": {
            "description": "Invalid JSON body."
          },
          "402": {
            "description": "Client limit reached."
          },
          "409": {
            "description": "User with this email already exists."
          },
          "422": {
            "description": "Validation failed."
          }
        }
      }
    },
    "/api/bookings": {
      "get": {
        "summary": "GET /api/bookings",
        "description": "List all bookings for the authenticated trainer.",
        "tags": [
          "bookings"
        ],
        "responses": {
          "200": {
            "description": "Successfully retrieved bookings.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "bookings": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "id": {
                            "type": "string"
                          },
                          "trainerId": {
                            "type": "string"
                          },
                          "startTime": {
                            "type": "string"
                          },
                          "endTime": {
                            "type": "string"
                          },
                          "status": {
                            "type": "string",
                            "enum": [
                              "PENDING",
                              "CONFIRMED",
                              "CANCELLED"
                            ]
                          },
                          "clientName": {
                            "type": "string",
                            "nullable": true
                          },
                          "clientEmail": {
                            "type": "string",
                            "nullable": true
                          },
                          "clientNotes": {
                            "type": "string",
                            "nullable": true
                          },
                          "clientId": {
                            "type": "string",
                            "nullable": true
                          }
                        },
                        "required": [
                          "id",
                          "trainerId",
                          "startTime",
                          "endTime",
                          "status"
                        ]
                      }
                    }
                  },
                  "required": [
                    "bookings"
                  ]
                }
              }
            }
          },
          "500": {
            "description": "Internal server error."
          }
        }
      },
      "post": {
        "summary": "POST /api/bookings",
        "description": "Create a new booking request. Requires client authentication.",
        "tags": [
          "bookings"
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "trainerId": {
                    "type": "string"
                  },
                  "startTime": {
                    "type": "string",
                    "format": "datetime"
                  },
                  "endTime": {
                    "type": "string",
                    "format": "datetime"
                  },
                  "clientNotes": {
                    "type": "string"
                  }
                },
                "required": [
                  "trainerId",
                  "startTime",
                  "endTime"
                ]
              }
            }
          }
        },
        "responses": {
          "201": {
            "description": "Booking created successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "booking": {
                      "type": "object",
                      "properties": {
                        "id": {
                          "type": "string"
                        },
                        "trainerId": {
                          "type": "string"
                        },
                        "startTime": {
                          "type": "string"
                        },
                        "endTime": {
                          "type": "string"
                        },
                        "status": {
                          "type": "string",
                          "enum": [
                            "PENDING",
                            "CONFIRMED",
                            "CANCELLED"
                          ]
                        },
                        "clientName": {
                          "type": "string",
                          "nullable": true
                        },
                        "clientEmail": {
                          "type": "string",
                          "nullable": true
                        },
                        "clientNotes": {
                          "type": "string",
                          "nullable": true
                        },
                        "clientId": {
                          "type": "string",
                          "nullable": true
                        }
                      },
                      "required": [
                        "id",
                        "trainerId",
                        "startTime",
                        "endTime",
                        "status"
                      ]
                    }
                  },
                  "required": [
                    "booking"
                  ]
                }
              }
            }
          },
          "401": {
            "description": "Unauthorized - authentication required."
          },
          "403": {
            "description": "Forbidden - only clients can create bookings."
          },
          "409": {
            "description": "Slot unavailable."
          },
          "422": {
            "description": "Validation failed."
          }
        }
      }
    },
    "/api/workout-sessions/start": {
      "post": {
        "summary": "POST /api/workout-sessions/start",
        "description": "Start a new workout session (or resume/start a planned one).",
        "tags": [
          "workout-sessions"
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "clientId": {
                    "type": "string"
                  },
                  "plannedSessionId": {
                    "type": "string"
                  },
                  "templateId": {
                    "type": "string"
                  }
                }
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "Session started successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "session": {
                      "type": "object",
                      "properties": {
                        "id": {
                          "type": "string"
                        },
                        "startTime": {
                          "type": "string"
                        },
                        "status": {
                          "type": "string"
                        },
                        "clientId": {
                          "type": "string"
                        }
                      },
                      "required": [
                        "id",
                        "startTime",
                        "status",
                        "clientId"
                      ]
                    }
                  },
                  "required": [
                    "session"
                  ]
                }
              }
            }
          },
          "400": {
            "description": "Invalid input or session already completed."
          },
          "403": {
            "description": "Unauthorized."
          },
          "404": {
            "description": "Planned session/booking not found."
          }
        }
      }
    },
    "/api/workout-sessions/live": {
      "get": {
        "summary": "GET /api/workout-sessions/live",
        "description": "Get the currently active workout session.",
        "tags": [
          "workout-sessions"
        ],
        "responses": {
          "200": {
            "description": "Active session retrieved successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "session": {
                      "nullable": true
                    }
                  },
                  "required": [
                    "session"
                  ]
                }
              }
            }
          }
        }
      },
      "post": {
        "summary": "POST /api/workout-sessions/live",
        "description": "Log an exercise set for a live session (create or update).",
        "tags": [
          "workout-sessions"
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "logId": {
                    "type": "string",
                    "nullable": true
                  },
                  "workoutSessionId": {
                    "type": "string"
                  },
                  "exerciseId": {
                    "type": "string"
                  },
                  "reps": {
                    "type": "number",
                    "example": 0
                  },
                  "weight": {
                    "type": "number",
                    "example": 0,
                    "nullable": true
                  },
                  "order": {
                    "type": "number",
                    "example": 0
                  },
                  "supersetKey": {
                    "type": "string",
                    "nullable": true
                  },
                  "orderInSuperset": {
                    "type": "number",
                    "example": 0,
                    "nullable": true
                  }
                },
                "required": [
                  "workoutSessionId",
                  "exerciseId",
                  "reps"
                ]
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "Successful update."
          },
          "201": {
            "description": "Created log."
          },
          "400": {
            "description": "Invalid request."
          },
          "401": {
            "description": "Unauthorized."
          },
          "403": {
            "description": "Forbidden."
          },
          "500": {
            "description": "Server error."
          }
        }
      }
    },
    "/api/workout-sessions/history": {
      "get": {
        "summary": "GET /api/workout-sessions/history",
        "description": "Get completed workout sessions history with cursor pagination.",
        "tags": [
          "workout-sessions"
        ],
        "parameters": [
          {
            "name": "clientId",
            "in": "query",
            "required": false,
            "schema": {
              "type": "string"
            }
          },
          {
            "name": "limit",
            "in": "query",
            "required": false,
            "schema": {
              "type": "number",
              "example": 0
            }
          },
          {
            "name": "cursor",
            "in": "query",
            "required": false,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "History retrieved successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "sessions": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "id": {
                            "type": "string"
                          },
                          "startTime": {
                            "type": "string"
                          },
                          "endTime": {
                            "type": "string",
                            "nullable": true
                          },
                          "status": {
                            "type": "string",
                            "enum": [
                              "COMPLETED"
                            ]
                          },
                          "client": {
                            "type": "object",
                            "properties": {
                              "id": {
                                "type": "string"
                              },
                              "name": {
                                "type": "string"
                              }
                            },
                            "required": [
                              "id",
                              "name"
                            ]
                          }
                        },
                        "required": [
                          "id",
                          "startTime",
                          "endTime",
                          "status",
                          "client"
                        ]
                      }
                    },
                    "nextCursor": {
                      "type": "string",
                      "nullable": true
                    },
                    "hasMore": {
                      "type": "boolean"
                    }
                  },
                  "required": [
                    "sessions",
                    "nextCursor",
                    "hasMore"
                  ]
                }
              }
            }
          },
          "400": {
            "description": "Client ID is required for trainers."
          }
        }
      }
    },
    "/api/workout-sessions/finish": {
      "post": {
        "summary": "POST /api/workout-sessions/finish",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "workout-sessions"
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "workoutSessionId": {
                    "type": "string"
                  },
                  "notes": {
                    "type": "string",
                    "nullable": true
                  }
                },
                "required": [
                  "workoutSessionId"
                ]
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "200 response."
          },
          "400": {
            "description": "400 response."
          },
          "422": {
            "description": "422 response."
          }
        }
      }
    },
    "/api/workout-sessions/{id}": {
      "get": {
        "summary": "GET /api/workout-sessions/{id}",
        "description": "Get details of a specific workout session.",
        "tags": [
          "workout-sessions"
        ],
        "parameters": [
          {
            "name": "id",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Session retrieved successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "session": {
                      "type": "object",
                      "properties": {
                        "id": {
                          "type": "string"
                        }
                      },
                      "required": [
                        "id"
                      ]
                    }
                  },
                  "required": [
                    "session"
                  ]
                }
              }
            }
          },
          "404": {
            "description": "Session not found."
          }
        }
      },
      "put": {
        "summary": "PUT /api/workout-sessions/{id}",
        "description": "Update workout session notes.",
        "tags": [
          "workout-sessions"
        ],
        "parameters": [
          {
            "name": "id",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "notes": {
                    "type": "string",
                    "nullable": true
                  }
                }
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "Session updated successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "session": {
                      "type": "object",
                      "properties": {
                        "id": {
                          "type": "string"
                        }
                      },
                      "required": [
                        "id"
                      ]
                    }
                  },
                  "required": [
                    "session"
                  ]
                }
              }
            }
          }
        }
      }
    },
    "/api/webhooks/stripe": {
      "post": {
        "summary": "POST /api/webhooks/stripe",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "webhooks"
        ],
        "parameters": [
          {
            "name": "Stripe-Signature",
            "in": "header",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "requestBody": {
          "required": true,
          "content": {
            "text/plain": {
              "schema": {
                "type": "string"
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "200 response."
          },
          "400": {
            "description": "400 response."
          }
        }
      }
    },
    "/api/workout/log": {
      "post": {
        "summary": "POST /api/workout/log",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "workout"
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "logId": {
                    "type": "string",
                    "nullable": true
                  },
                  "workoutSessionId": {
                    "type": "string"
                  },
                  "exerciseId": {
                    "type": "string"
                  },
                  "reps": {
                    "type": "number",
                    "example": 0
                  },
                  "weight": {
                    "type": "number",
                    "example": 0,
                    "nullable": true
                  },
                  "order": {
                    "type": "number",
                    "example": 0
                  }
                },
                "required": [
                  "workoutSessionId",
                  "exerciseId",
                  "reps"
                ]
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "200 response."
          },
          "400": {
            "description": "400 response."
          },
          "422": {
            "description": "422 response."
          }
        }
      }
    },
    "/api/user/tier": {
      "get": {
        "summary": "GET /api/user/tier",
        "description": "Get the current user's subscription tier.",
        "tags": [
          "user"
        ],
        "responses": {
          "200": {
            "description": "Tier retrieved successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "tier": {
                      "type": "string"
                    },
                    "isFreeMode": {
                      "type": "boolean"
                    }
                  },
                  "required": [
                    "tier",
                    "isFreeMode"
                  ]
                }
              }
            }
          },
          "401": {
            "description": "Unauthorized."
          },
          "404": {
            "description": "User not found."
          }
        }
      }
    },
    "/api/user/delete": {
      "delete": {
        "summary": "DELETE /api/user/delete",
        "description": "Delete the authenticated user account.",
        "tags": [
          "user"
        ],
        "responses": {
          "200": {
            "description": "User deleted successfully."
          },
          "401": {
            "description": "Unauthorized."
          }
        }
      }
    },
    "/api/trainers/specialties": {
      "get": {
        "summary": "GET /api/trainers/specialties",
        "description": "Get list of available specialties.",
        "tags": [
          "trainers"
        ],
        "responses": {
          "200": {
            "description": "Specialties retrieved successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "specialties": {
                      "type": "array",
                      "items": {
                        "type": "string"
                      }
                    }
                  },
                  "required": [
                    "specialties"
                  ]
                }
              }
            }
          }
        }
      }
    },
    "/api/trainers/{username}": {
      "get": {
        "summary": "GET /api/trainers/{username}",
        "description": "Get public profile details for a trainer.",
        "tags": [
          "trainers"
        ],
        "parameters": [
          {
            "name": "username",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Trainer profile retrieved successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "id": {
                      "type": "string"
                    },
                    "name": {
                      "type": "string",
                      "nullable": true
                    },
                    "username": {
                      "type": "string",
                      "nullable": true
                    },
                    "profile": {},
                    "stats": {
                      "type": "object",
                      "properties": {
                        "clientsCoached": {
                          "type": "number",
                          "example": 0
                        },
                        "reviewCount": {
                          "type": "number",
                          "example": 0
                        },
                        "averageRating": {
                          "type": "number",
                          "example": 0,
                          "nullable": true
                        }
                      },
                      "required": [
                        "clientsCoached",
                        "reviewCount",
                        "averageRating"
                      ]
                    }
                  },
                  "required": [
                    "id",
                    "name",
                    "username",
                    "profile",
                    "stats"
                  ]
                }
              }
            }
          },
          "404": {
            "description": "Trainer not found."
          }
        }
      }
    },
    "/api/trainer/settings": {
      "get": {
        "summary": "GET /api/trainer/settings",
        "description": "Get trainer settings.",
        "tags": [
          "trainer"
        ],
        "responses": {
          "200": {
            "description": "Settings retrieved successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "defaultCheckInDay": {
                      "type": "number",
                      "example": 0
                    },
                    "defaultCheckInHour": {
                      "type": "number",
                      "example": 0
                    }
                  },
                  "required": [
                    "defaultCheckInDay",
                    "defaultCheckInHour"
                  ]
                }
              }
            }
          }
        }
      },
      "patch": {
        "summary": "PATCH /api/trainer/settings",
        "description": "Update trainer settings.",
        "tags": [
          "trainer"
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "defaultCheckInDay": {
                    "type": "number",
                    "example": 0
                  },
                  "defaultCheckInHour": {
                    "type": "number",
                    "example": 0
                  }
                }
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "Settings updated successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "defaultCheckInDay": {
                      "type": "number",
                      "example": 0
                    },
                    "defaultCheckInHour": {
                      "type": "number",
                      "example": 0
                    }
                  },
                  "required": [
                    "defaultCheckInDay",
                    "defaultCheckInHour"
                  ]
                }
              }
            }
          }
        }
      }
    },
    "/api/trainer/session-creation-data": {
      "get": {
        "summary": "GET /api/trainer/session-creation-data",
        "description": "Get lightweight data (clients, templates) for creating sessions.",
        "tags": [
          "trainer"
        ],
        "responses": {
          "200": {
            "description": "Data retrieved successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "clients": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "id": {
                            "type": "string"
                          },
                          "name": {
                            "type": "string"
                          }
                        },
                        "required": [
                          "id",
                          "name"
                        ]
                      }
                    },
                    "templates": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "id": {
                            "type": "string"
                          },
                          "name": {
                            "type": "string"
                          },
                          "program": {
                            "type": "object",
                            "properties": {
                              "trainerId": {
                                "type": "string",
                                "nullable": true
                              }
                            },
                            "required": [
                              "trainerId"
                            ]
                          }
                        },
                        "required": [
                          "id",
                          "name",
                          "program"
                        ]
                      }
                    }
                  },
                  "required": [
                    "clients",
                    "templates"
                  ]
                }
              }
            }
          }
        }
      }
    },
    "/api/trainer/programs": {
      "get": {
        "summary": "GET /api/trainer/programs",
        "description": "Get trainer's program library (programs and templates).",
        "tags": [
          "trainer"
        ],
        "parameters": [
          {
            "name": "lightweight",
            "in": "query",
            "required": false,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Library retrieved successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "userPrograms": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "id": {
                            "type": "string"
                          },
                          "name": {
                            "type": "string"
                          },
                          "description": {
                            "type": "string",
                            "nullable": true
                          },
                          "templates": {
                            "type": "array",
                            "items": {},
                            "nullable": true
                          }
                        },
                        "required": [
                          "id",
                          "name",
                          "description",
                          "templates"
                        ]
                      }
                    },
                    "systemPrograms": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "id": {
                            "type": "string"
                          },
                          "name": {
                            "type": "string"
                          },
                          "description": {
                            "type": "string",
                            "nullable": true
                          },
                          "templates": {
                            "type": "array",
                            "items": {},
                            "nullable": true
                          }
                        },
                        "required": [
                          "id",
                          "name",
                          "description",
                          "templates"
                        ]
                      }
                    },
                    "userTemplates": {
                      "type": "array",
                      "items": {}
                    },
                    "systemTemplates": {
                      "type": "array",
                      "items": {}
                    }
                  }
                }
              }
            }
          }
        }
      },
      "post": {
        "summary": "POST /api/trainer/programs",
        "description": "Create a new workout program.",
        "tags": [
          "trainer"
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "name": {
                    "type": "string",
                    "minLength": 1
                  },
                  "description": {
                    "type": "string",
                    "nullable": true
                  }
                },
                "required": [
                  "name"
                ]
              }
            }
          }
        },
        "responses": {
          "201": {
            "description": "Program created successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "program": {}
                  },
                  "required": [
                    "program"
                  ]
                }
              }
            }
          },
          "422": {
            "description": "Validation failed."
          }
        }
      }
    },
    "/api/trainer/profile": {
      "get": {
        "summary": "GET /api/trainer/profile",
        "description": "Get the full profile for the authenticated trainer (Alias).",
        "tags": [
          "trainer"
        ],
        "responses": {
          "200": {
            "description": "Profile retrieved successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "profile": {
                      "type": "object",
                      "properties": {
                        "id": {
                          "type": "string"
                        },
                        "name": {
                          "type": "string",
                          "nullable": true
                        },
                        "email": {
                          "type": "string"
                        },
                        "username": {
                          "type": "string",
                          "nullable": true
                        },
                        "role": {
                          "type": "string"
                        },
                        "profile": {},
                        "packages": {
                          "type": "array",
                          "items": {}
                        }
                      },
                      "required": [
                        "id",
                        "name",
                        "email",
                        "username",
                        "role",
                        "profile",
                        "packages"
                      ],
                      "nullable": true
                    }
                  },
                  "required": [
                    "profile"
                  ]
                }
              }
            }
          }
        }
      }
    },
    "/api/trainer/clients": {
      "get": {
        "summary": "GET /api/trainer/clients",
        "description": "List all clients for the authenticated trainer (Alias).",
        "tags": [
          "trainer"
        ],
        "responses": {
          "200": {
            "description": "Successfully retrieved client list.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "clients": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "id": {
                            "type": "string"
                          },
                          "name": {
                            "type": "string"
                          },
                          "email": {
                            "type": "string",
                            "nullable": true
                          },
                          "phone": {
                            "type": "string",
                            "nullable": true
                          },
                          "status": {
                            "type": "string",
                            "enum": [
                              "active",
                              "inactive",
                              "pending"
                            ]
                          },
                          "userId": {
                            "type": "string"
                          },
                          "lastWorkoutDate": {
                            "type": "string"
                          },
                          "engagementScore": {
                            "type": "string",
                            "enum": [
                              "high",
                              "medium",
                              "low"
                            ]
                          }
                        },
                        "required": [
                          "id",
                          "name",
                          "status",
                          "engagementScore"
                        ]
                      }
                    },
                    "isPremium": {
                      "type": "boolean"
                    }
                  },
                  "required": [
                    "clients",
                    "isPremium"
                  ]
                }
              }
            }
          }
        }
      },
      "post": {
        "summary": "POST /api/trainer/clients",
        "description": "Create a new client (Alias).",
        "tags": [
          "trainer"
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "name": {
                    "type": "string",
                    "minLength": 1
                  },
                  "email": {},
                  "phone": {},
                  "status": {
                    "type": "string",
                    "enum": [
                      "active",
                      "inactive",
                      "pending"
                    ]
                  }
                },
                "required": [
                  "name",
                  "email",
                  "phone"
                ]
              }
            }
          }
        },
        "responses": {
          "201": {
            "description": "Successfully created client."
          },
          "400": {
            "description": "Invalid JSON body."
          }
        }
      }
    },
    "/api/trainer/check-ins": {
      "get": {
        "summary": "List Trainer's Check-ins",
        "description": "Retrieves a list of check-ins for the authenticated trainer, filtered by status.",
        "tags": [
          "trainer"
        ],
        "parameters": [
          {
            "name": "status",
            "in": "query",
            "required": false,
            "schema": {
              "type": "string",
              "enum": [
                "SUBMITTED",
                "REVIEWED"
              ]
            }
          }
        ],
        "responses": {
          "200": {
            "description": "List of check-ins with basic client info.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "array",
                  "items": {
                    "type": "object",
                    "properties": {
                      "id": {
                        "type": "string"
                      },
                      "clientId": {
                        "type": "string"
                      },
                      "date": {},
                      "status": {
                        "type": "string",
                        "enum": [
                          "SUBMITTED",
                          "REVIEWED"
                        ]
                      },
                      "trainerResponse": {
                        "type": "string",
                        "nullable": true
                      },
                      "reviewedAt": {
                        "nullable": true
                      },
                      "client": {
                        "type": "object",
                        "properties": {
                          "id": {
                            "type": "string"
                          },
                          "name": {
                            "type": "string"
                          },
                          "avatarPath": {
                            "type": "string",
                            "nullable": true
                          }
                        },
                        "required": [
                          "id",
                          "name",
                          "avatarPath"
                        ]
                      }
                    },
                    "required": [
                      "id",
                      "clientId",
                      "date",
                      "status",
                      "trainerResponse",
                      "reviewedAt",
                      "client"
                    ]
                  }
                }
              }
            }
          },
          "401": {
            "description": "Unauthorized - Missing or invalid token."
          },
          "403": {
            "description": "Forbidden - User is not a trainer."
          }
        }
      }
    },
    "/api/trainer/calendar": {
      "get": {
        "summary": "GET /api/trainer/calendar",
        "description": "Get calendar events within a date range.",
        "tags": [
          "trainer"
        ],
        "parameters": [
          {
            "name": "startDate",
            "in": "query",
            "required": true,
            "schema": {
              "type": "string"
            }
          },
          {
            "name": "endDate",
            "in": "query",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Events retrieved successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "events": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "id": {
                            "type": "string"
                          },
                          "title": {
                            "type": "string"
                          },
                          "start": {
                            "type": "string"
                          },
                          "end": {
                            "type": "string"
                          },
                          "type": {
                            "type": "string",
                            "enum": [
                              "booking",
                              "session_planned",
                              "session_completed",
                              "session_in_progress"
                            ]
                          },
                          "clientId": {
                            "type": "string"
                          },
                          "clientName": {
                            "type": "string"
                          }
                        },
                        "required": [
                          "id",
                          "title",
                          "start",
                          "end",
                          "type",
                          "clientName"
                        ]
                      }
                    }
                  },
                  "required": [
                    "events"
                  ]
                }
              }
            }
          },
          "400": {
            "description": "Missing start/end date."
          }
        }
      },
      "post": {
        "summary": "POST /api/trainer/calendar",
        "description": "Schedule a new session.",
        "tags": [
          "trainer"
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "clientId": {
                    "type": "string",
                    "minLength": 1
                  },
                  "startTime": {
                    "type": "string"
                  },
                  "endTime": {
                    "type": "string"
                  },
                  "notes": {
                    "type": "string",
                    "nullable": true
                  },
                  "templateId": {
                    "type": "string",
                    "nullable": true
                  },
                  "repeats": {
                    "type": "boolean"
                  },
                  "repeatWeeks": {
                    "type": "number",
                    "example": 0
                  },
                  "repeatDays": {
                    "type": "string"
                  }
                },
                "required": [
                  "clientId",
                  "startTime",
                  "endTime"
                ]
              }
            }
          }
        },
        "responses": {
          "201": {
            "description": "Session(s) created successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "message": {
                      "type": "string"
                    }
                  },
                  "required": [
                    "message"
                  ]
                }
              }
            }
          },
          "400": {
            "description": "Invalid input."
          },
          "409": {
            "description": "Conflict detected."
          }
        }
      }
    },
    "/api/trainer/assessments": {
      "get": {
        "summary": "GET /api/trainer/assessments",
        "description": "List all assessments available to the trainer.",
        "tags": [
          "trainer"
        ],
        "responses": {
          "200": {
            "description": "Assessments retrieved successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "assessments": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "id": {
                            "type": "string"
                          },
                          "name": {
                            "type": "string"
                          },
                          "description": {
                            "type": "string",
                            "nullable": true
                          },
                          "unit": {
                            "type": "string"
                          },
                          "trainerId": {
                            "type": "string",
                            "nullable": true
                          }
                        },
                        "required": [
                          "id",
                          "name",
                          "description",
                          "unit",
                          "trainerId"
                        ]
                      }
                    }
                  },
                  "required": [
                    "assessments"
                  ]
                }
              }
            }
          }
        }
      }
    },
    "/api/sync/push": {
      "post": {
        "summary": "POST /api/sync/push",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "sync"
        ],
        "responses": {
          "200": {
            "description": "200 response."
          },
          "400": {
            "description": "400 response."
          }
        }
      }
    },
    "/api/sync/pull": {
      "get": {
        "summary": "GET /api/sync/pull",
        "description": "Pull changes for WatermelonDB sync.",
        "tags": [
          "sync"
        ],
        "parameters": [
          {
            "name": "last_pulled_at",
            "in": "query",
            "required": false,
            "schema": {
              "type": "string"
            }
          },
          {
            "name": "schemaVersion",
            "in": "query",
            "required": false,
            "schema": {
              "type": "string"
            }
          },
          {
            "name": "migration",
            "in": "query",
            "required": false,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Changes retrieved successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "changes": {},
                    "timestamp": {
                      "type": "number",
                      "example": 0
                    }
                  },
                  "required": [
                    "changes",
                    "timestamp"
                  ]
                }
              }
            }
          }
        }
      }
    },
    "/api/profile/me": {
      "get": {
        "summary": "GET /api/profile/me",
        "description": "Get the full profile for the authenticated user.",
        "tags": [
          "profile"
        ],
        "responses": {
          "200": {
            "description": "Profile retrieved successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "profile": {
                      "type": "object",
                      "properties": {
                        "id": {
                          "type": "string"
                        },
                        "name": {
                          "type": "string",
                          "nullable": true
                        },
                        "email": {
                          "type": "string"
                        },
                        "username": {
                          "type": "string",
                          "nullable": true
                        },
                        "role": {
                          "type": "string"
                        },
                        "profile": {
                          "type": "object",
                          "properties": {
                            "id": {
                              "type": "string"
                            },
                            "aboutMe": {
                              "type": "string",
                              "nullable": true
                            }
                          },
                          "required": [
                            "id",
                            "aboutMe"
                          ],
                          "nullable": true
                        },
                        "packages": {
                          "type": "array",
                          "items": {}
                        }
                      },
                      "required": [
                        "id",
                        "name",
                        "email",
                        "username",
                        "role",
                        "profile",
                        "packages"
                      ],
                      "nullable": true
                    }
                  },
                  "required": [
                    "profile"
                  ]
                }
              }
            }
          }
        }
      }
    },
    "/api/notifications/{id}": {
      "put": {
        "summary": "PUT /api/notifications/{id}",
        "description": "Mark a notification as read.",
        "tags": [
          "notifications"
        ],
        "parameters": [
          {
            "name": "id",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Notification updated successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "notification": {
                      "type": "object",
                      "properties": {
                        "id": {
                          "type": "string"
                        },
                        "readStatus": {
                          "type": "boolean"
                        }
                      },
                      "required": [
                        "id",
                        "readStatus"
                      ]
                    }
                  },
                  "required": [
                    "notification"
                  ]
                }
              }
            }
          }
        }
      }
    },
    "/api/exercises/find-media": {
      "get": {
        "summary": "GET /api/exercises/find-media",
        "description": "Find a media URL (GIF/Video) for a given exercise name.",
        "tags": [
          "exercises"
        ],
        "parameters": [
          {
            "name": "name",
            "in": "query",
            "required": true,
            "schema": {
              "type": "string",
              "minLength": 1
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Media URL found or null.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "mediaUrl": {
                      "type": "string",
                      "nullable": true
                    }
                  },
                  "required": [
                    "mediaUrl"
                  ]
                }
              }
            }
          },
          "400": {
            "description": "Name parameter is required."
          }
        }
      }
    },
    "/api/dashboard/summary": {
      "get": {
        "summary": "GET /api/dashboard/summary",
        "description": "Get aggregated summary data for the trainer dashboard.",
        "tags": [
          "dashboard"
        ],
        "responses": {
          "200": {
            "description": "Dashboard summary retrieved successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "profileChecklist": {
                      "type": "object",
                      "properties": {
                        "completionPercentage": {
                          "type": "number",
                          "example": 0
                        },
                        "missingFields": {
                          "type": "array",
                          "items": {
                            "type": "string"
                          }
                        },
                        "profile": {
                          "type": "object",
                          "properties": {
                            "profilePhotoPath": {
                              "type": "string",
                              "nullable": true
                            },
                            "bannerImagePath": {
                              "type": "string",
                              "nullable": true
                            },
                            "aboutMe": {
                              "type": "string",
                              "nullable": true
                            }
                          },
                          "nullable": true
                        }
                      },
                      "required": [
                        "completionPercentage",
                        "missingFields",
                        "profile"
                      ]
                    },
                    "upcomingSessions": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "id": {
                            "type": "string"
                          },
                          "type": {
                            "type": "string",
                            "enum": [
                              "booking",
                              "session"
                            ]
                          },
                          "startTime": {
                            "type": "string"
                          },
                          "clientName": {
                            "type": "string",
                            "nullable": true
                          },
                          "title": {
                            "type": "string"
                          },
                          "clientId": {
                            "type": "string"
                          }
                        },
                        "required": [
                          "id",
                          "type",
                          "startTime",
                          "clientName",
                          "title",
                          "clientId"
                        ]
                      }
                    },
                    "activityFeed": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "type": {
                            "type": "string",
                            "enum": [
                              "NEW_MEASUREMENT",
                              "PROGRESS_PHOTO",
                              "WORKOUT_COMPLETED"
                            ]
                          },
                          "date": {
                            "type": "string"
                          },
                          "clientName": {
                            "type": "string"
                          }
                        },
                        "required": [
                          "type",
                          "date",
                          "clientName"
                        ]
                      }
                    },
                    "businessPerformance": {
                      "type": "object",
                      "properties": {
                        "currentMonth": {
                          "type": "object",
                          "properties": {
                            "revenue": {
                              "type": "number",
                              "example": 0
                            },
                            "newClients": {
                              "type": "number",
                              "example": 0
                            },
                            "completedSessions": {
                              "type": "number",
                              "example": 0
                            },
                            "totalSessions": {
                              "type": "number",
                              "example": 0
                            }
                          },
                          "required": [
                            "revenue",
                            "newClients",
                            "completedSessions",
                            "totalSessions"
                          ]
                        },
                        "previousMonth": {
                          "type": "object",
                          "properties": {
                            "revenue": {
                              "type": "number",
                              "example": 0
                            },
                            "newClients": {
                              "type": "number",
                              "example": 0
                            },
                            "completedSessions": {
                              "type": "number",
                              "example": 0
                            },
                            "totalSessions": {
                              "type": "number",
                              "example": 0
                            }
                          },
                          "required": [
                            "revenue",
                            "newClients",
                            "completedSessions",
                            "totalSessions"
                          ]
                        }
                      },
                      "required": [
                        "currentMonth",
                        "previousMonth"
                      ]
                    },
                    "clientEngagement": {
                      "type": "object",
                      "properties": {
                        "active": {
                          "type": "number",
                          "example": 0
                        },
                        "slipping": {
                          "type": "number",
                          "example": 0
                        },
                        "atRisk": {
                          "type": "number",
                          "example": 0
                        },
                        "retentionRate": {
                          "type": "number",
                          "example": 0
                        },
                        "pendingActionsDetails": {
                          "type": "array",
                          "items": {}
                        }
                      },
                      "required": [
                        "active",
                        "slipping",
                        "atRisk"
                      ]
                    },
                    "servicePopularity": {
                      "type": "object",
                      "properties": {
                        "popularPackages": {
                          "type": "array",
                          "items": {
                            "type": "object",
                            "properties": {
                              "name": {
                                "type": "string"
                              },
                              "count": {
                                "type": "number",
                                "example": 0
                              }
                            },
                            "required": [
                              "name",
                              "count"
                            ]
                          }
                        },
                        "popularTemplates": {
                          "type": "array",
                          "items": {
                            "type": "object",
                            "properties": {
                              "name": {
                                "type": "string"
                              },
                              "count": {
                                "type": "number",
                                "example": 0
                              }
                            },
                            "required": [
                              "name",
                              "count"
                            ]
                          }
                        }
                      },
                      "required": [
                        "popularPackages",
                        "popularTemplates"
                      ]
                    },
                    "userTier": {
                      "type": "string"
                    }
                  },
                  "required": [
                    "profileChecklist",
                    "upcomingSessions",
                    "activityFeed",
                    "businessPerformance",
                    "clientEngagement",
                    "servicePopularity"
                  ]
                }
              }
            }
          }
        }
      }
    },
    "/api/dashboard/insights": {
      "get": {
        "summary": "GET /api/dashboard/insights",
        "description": "Get AI-generated insights for the trainer dashboard.",
        "tags": [
          "dashboard"
        ],
        "responses": {
          "200": {
            "description": "Insights retrieved successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "insights": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "type": {
                            "type": "string",
                            "enum": [
                              "opportunity",
                              "risk",
                              "operational"
                            ]
                          },
                          "text": {
                            "type": "string"
                          }
                        },
                        "required": [
                          "type",
                          "text"
                        ]
                      }
                    }
                  },
                  "required": [
                    "insights"
                  ]
                }
              }
            }
          }
        }
      }
    },
    "/api/cron/trial-reminders": {
      "get": {
        "summary": "GET /api/cron/trial-reminders",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "cron"
        ],
        "responses": {
          "200": {
            "description": "Successful response."
          }
        }
      }
    },
    "/api/cron/check-in-reminder": {
      "get": {
        "summary": "GET /api/cron/check-in-reminder",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "cron"
        ],
        "responses": {
          "200": {
            "description": "Successful response."
          }
        }
      }
    },
    "/api/clients/request-link": {
      "post": {
        "summary": "POST /api/clients/request-link",
        "description": "Request to link to an existing client user by email.",
        "tags": [
          "clients"
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "email": {
                    "type": "string",
                    "format": "email"
                  }
                },
                "required": [
                  "email"
                ]
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "Connection request sent.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "message": {
                      "type": "string"
                    }
                  },
                  "required": [
                    "message"
                  ]
                }
              }
            }
          },
          "400": {
            "description": "Client email is required."
          },
          "404": {
            "description": "No user found with this email."
          }
        }
      }
    },
    "/api/clients/{id}": {
      "get": {
        "summary": "GET /api/clients/{id}",
        "description": "Retrieve detailed information for a specific client.",
        "tags": [
          "clients"
        ],
        "parameters": [
          {
            "name": "id",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Successfully retrieved client information.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "client": {
                      "type": "object",
                      "properties": {
                        "id": {
                          "type": "string"
                        },
                        "name": {
                          "type": "string"
                        },
                        "email": {
                          "type": "string",
                          "nullable": true
                        },
                        "phone": {
                          "type": "string",
                          "nullable": true
                        },
                        "status": {
                          "type": "string",
                          "enum": [
                            "active",
                            "inactive",
                            "pending"
                          ]
                        },
                        "createdAt": {
                          "type": "string"
                        },
                        "checkInDay": {
                          "type": "number",
                          "example": 0,
                          "nullable": true
                        },
                        "checkInHour": {
                          "type": "number",
                          "example": 0,
                          "nullable": true
                        },
                        "trainerId": {
                          "type": "string"
                        },
                        "avatarUrl": {
                          "type": "string"
                        }
                      },
                      "required": [
                        "id",
                        "name",
                        "email",
                        "phone",
                        "status",
                        "createdAt",
                        "checkInDay",
                        "checkInHour",
                        "trainerId"
                      ]
                    }
                  },
                  "required": [
                    "client"
                  ]
                }
              }
            }
          },
          "400": {
            "description": "Client ID is required."
          },
          "404": {
            "description": "Client not found."
          }
        }
      },
      "put": {
        "summary": "PUT /api/clients/{id}",
        "description": "Update client information.",
        "tags": [
          "clients"
        ],
        "parameters": [
          {
            "name": "id",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "name": {
                    "type": "string",
                    "minLength": 1
                  },
                  "email": {
                    "type": "string",
                    "format": "email"
                  },
                  "phone": {
                    "type": "string",
                    "minLength": 1
                  },
                  "status": {
                    "type": "string",
                    "enum": [
                      "active",
                      "inactive",
                      "pending"
                    ]
                  },
                  "checkInDay": {
                    "type": "number",
                    "example": 0,
                    "nullable": true
                  },
                  "checkInHour": {
                    "type": "number",
                    "example": 0,
                    "nullable": true
                  }
                }
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "Successfully updated client information.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "client": {
                      "type": "object",
                      "properties": {
                        "id": {
                          "type": "string"
                        },
                        "name": {
                          "type": "string"
                        },
                        "email": {
                          "type": "string",
                          "nullable": true
                        },
                        "phone": {
                          "type": "string",
                          "nullable": true
                        },
                        "status": {
                          "type": "string",
                          "enum": [
                            "active",
                            "inactive",
                            "pending"
                          ]
                        },
                        "createdAt": {
                          "type": "string"
                        },
                        "checkInDay": {
                          "type": "number",
                          "example": 0,
                          "nullable": true
                        },
                        "checkInHour": {
                          "type": "number",
                          "example": 0,
                          "nullable": true
                        },
                        "trainerId": {
                          "type": "string"
                        },
                        "avatarUrl": {
                          "type": "string"
                        }
                      },
                      "required": [
                        "id",
                        "name",
                        "email",
                        "phone",
                        "status",
                        "createdAt",
                        "checkInDay",
                        "checkInHour",
                        "trainerId"
                      ]
                    }
                  },
                  "required": [
                    "client"
                  ]
                }
              }
            }
          },
          "400": {
            "description": "Client ID is required or invalid request body."
          },
          "404": {
            "description": "Client not found."
          },
          "422": {
            "description": "Validation failed."
          }
        }
      },
      "delete": {
        "summary": "DELETE /api/clients/{id}",
        "description": "Delete a client.",
        "tags": [
          "clients"
        ],
        "parameters": [
          {
            "name": "id",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Successfully deleted client.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "message": {
                      "type": "string"
                    }
                  },
                  "required": [
                    "message"
                  ]
                }
              }
            }
          },
          "400": {
            "description": "Client ID is required."
          }
        }
      }
    },
    "/api/client/trainer": {
      "get": {
        "summary": "GET /api/client/trainer",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "client"
        ],
        "responses": {
          "200": {
            "description": "Successful response."
          }
        }
      },
      "delete": {
        "summary": "DELETE /api/client/trainer",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "client"
        ],
        "responses": {
          "200": {
            "description": "Successful response."
          }
        }
      }
    },
    "/api/client/progress": {
      "get": {
        "summary": "GET /api/client/progress",
        "description": "Get progress metrics for the authenticated client.",
        "tags": [
          "client"
        ],
        "responses": {
          "200": {
            "description": "Progress data retrieved successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "weight": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "date": {},
                          "value": {
                            "type": "number",
                            "example": 0
                          }
                        },
                        "required": [
                          "date",
                          "value"
                        ]
                      }
                    },
                    "bodyFat": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "date": {},
                          "value": {
                            "type": "number",
                            "example": 0
                          }
                        },
                        "required": [
                          "date",
                          "value"
                        ]
                      }
                    },
                    "volume": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "date": {},
                          "value": {
                            "type": "number",
                            "example": 0
                          }
                        },
                        "required": [
                          "date",
                          "value"
                        ]
                      }
                    },
                    "exercisePerformance": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "exerciseId": {
                            "type": "string"
                          },
                          "exerciseName": {
                            "type": "string"
                          },
                          "maxWeight": {
                            "type": "number",
                            "example": 0
                          },
                          "maxReps": {
                            "type": "number",
                            "example": 0
                          },
                          "maxVolume": {
                            "type": "number",
                            "example": 0
                          },
                          "lastPerformed": {
                            "type": "string"
                          }
                        },
                        "required": [
                          "exerciseId",
                          "exerciseName",
                          "maxWeight",
                          "maxReps",
                          "maxVolume",
                          "lastPerformed"
                        ]
                      }
                    },
                    "favoriteExercises": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "exerciseId": {
                            "type": "string"
                          },
                          "exerciseName": {
                            "type": "string"
                          },
                          "frequency": {
                            "type": "number",
                            "example": 0
                          }
                        },
                        "required": [
                          "exerciseId",
                          "exerciseName",
                          "frequency"
                        ]
                      }
                    },
                    "worstPerformingExercises": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "exerciseId": {
                            "type": "string"
                          },
                          "exerciseName": {
                            "type": "string"
                          },
                          "issue": {
                            "type": "string"
                          }
                        },
                        "required": [
                          "exerciseId",
                          "exerciseName",
                          "issue"
                        ]
                      }
                    }
                  },
                  "required": [
                    "weight",
                    "bodyFat",
                    "volume",
                    "exercisePerformance",
                    "favoriteExercises",
                    "worstPerformingExercises"
                  ]
                }
              }
            }
          }
        }
      }
    },
    "/api/client/dashboard": {
      "get": {
        "summary": "GET /api/client/dashboard",
        "description": "Get the dashboard data for the authenticated client.",
        "tags": [
          "client"
        ],
        "responses": {
          "200": {
            "description": "Dashboard data retrieved successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "clientData": {
                      "type": "object",
                      "properties": {
                        "id": {
                          "type": "string"
                        },
                        "name": {
                          "type": "string"
                        },
                        "email": {
                          "type": "string"
                        },
                        "trainer": {
                          "type": "object",
                          "properties": {
                            "name": {
                              "type": "string",
                              "nullable": true
                            },
                            "username": {
                              "type": "string"
                            },
                            "email": {
                              "type": "string"
                            }
                          },
                          "required": [
                            "name",
                            "username",
                            "email"
                          ],
                          "nullable": true
                        },
                        "workoutSessions": {
                          "type": "array",
                          "items": {
                            "type": "object",
                            "properties": {
                              "id": {
                                "type": "string"
                              },
                              "startTime": {
                                "type": "string"
                              },
                              "endTime": {
                                "type": "string",
                                "nullable": true
                              },
                              "status": {
                                "type": "string"
                              },
                              "name": {
                                "type": "string",
                                "nullable": true
                              },
                              "exerciseLogs": {
                                "type": "array",
                                "items": {}
                              }
                            },
                            "required": [
                              "id",
                              "startTime",
                              "endTime",
                              "status",
                              "name",
                              "exerciseLogs"
                            ]
                          }
                        },
                        "measurements": {
                          "type": "array",
                          "items": {
                            "type": "object",
                            "properties": {
                              "id": {
                                "type": "string"
                              },
                              "measurementDate": {
                                "type": "string"
                              },
                              "weightKg": {
                                "type": "number",
                                "example": 0,
                                "nullable": true
                              },
                              "bodyFatPercentage": {
                                "type": "number",
                                "example": 0,
                                "nullable": true
                              }
                            },
                            "required": [
                              "id",
                              "measurementDate",
                              "weightKg",
                              "bodyFatPercentage"
                            ]
                          }
                        }
                      },
                      "required": [
                        "id",
                        "name",
                        "email",
                        "trainer",
                        "workoutSessions",
                        "measurements"
                      ]
                    },
                    "weightUnit": {
                      "type": "string"
                    },
                    "upcomingClientSessions": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "id": {
                            "type": "string"
                          },
                          "title": {
                            "type": "string"
                          },
                          "date": {
                            "type": "string"
                          },
                          "duration": {
                            "type": "number",
                            "example": 0
                          }
                        },
                        "required": [
                          "id",
                          "title",
                          "date",
                          "duration"
                        ]
                      }
                    }
                  },
                  "required": [
                    "clientData",
                    "weightUnit",
                    "upcomingClientSessions"
                  ]
                }
              }
            }
          },
          "404": {
            "description": "Client profile not found."
          }
        }
      }
    },
    "/api/client/check-ins": {
      "get": {
        "summary": "GET /api/client/check-ins",
        "description": "List past check-ins for the authenticated client.",
        "tags": [
          "client"
        ],
        "responses": {
          "200": {
            "description": "List of check-in summaries.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "array",
                  "items": {
                    "type": "object",
                    "properties": {
                      "id": {
                        "type": "string"
                      },
                      "date": {},
                      "status": {
                        "type": "string"
                      },
                      "hasFeedback": {
                        "type": "boolean"
                      },
                      "photoCount": {
                        "type": "number",
                        "example": 0
                      },
                      "reviewedAt": {
                        "nullable": true
                      }
                    },
                    "required": [
                      "id",
                      "date",
                      "status",
                      "hasFeedback",
                      "photoCount",
                      "reviewedAt"
                    ]
                  }
                }
              }
            }
          },
          "401": {
            "description": "Unauthorized."
          },
          "404": {
            "description": "Client profile not found."
          }
        }
      }
    },
    "/api/client/check-in": {
      "post": {
        "summary": "POST /api/client/check-in",
        "description": "Submit a weekly check-in.",
        "tags": [
          "client"
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "weight": {
                    "type": "number",
                    "example": 0,
                    "nullable": true
                  },
                  "waistCm": {
                    "type": "number",
                    "example": 0,
                    "nullable": true
                  },
                  "sleepHours": {
                    "type": "number",
                    "example": 0,
                    "nullable": true
                  },
                  "energyLevel": {
                    "type": "number",
                    "example": 0,
                    "nullable": true
                  },
                  "stressLevel": {
                    "type": "number",
                    "example": 0,
                    "nullable": true
                  },
                  "hungerLevel": {
                    "type": "number",
                    "example": 0,
                    "nullable": true
                  },
                  "digestionLevel": {
                    "type": "number",
                    "example": 0,
                    "nullable": true
                  },
                  "nutritionCompliance": {
                    "type": "string",
                    "nullable": true
                  },
                  "clientNotes": {
                    "type": "string"
                  },
                  "photos": {
                    "type": "array",
                    "items": {
                      "type": "object",
                      "properties": {
                        "imagePath": {
                          "type": "string"
                        },
                        "caption": {
                          "type": "string"
                        },
                        "date": {
                          "type": "string"
                        }
                      },
                      "required": [
                        "imagePath"
                      ]
                    }
                  }
                }
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "Check-in submitted successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "id": {
                      "type": "string"
                    },
                    "status": {
                      "type": "string"
                    }
                  },
                  "required": [
                    "id",
                    "status"
                  ]
                }
              }
            }
          },
          "404": {
            "description": "Client profile not found."
          }
        }
      }
    },
    "/api/checkout/session": {
      "post": {
        "summary": "POST /api/checkout/session",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "checkout"
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "packageId": {
                    "type": "string"
                  }
                },
                "required": [
                  "packageId"
                ]
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "200 response."
          },
          "400": {
            "description": "400 response."
          },
          "422": {
            "description": "422 response."
          }
        }
      }
    },
    "/api/billing/subscription": {
      "get": {
        "summary": "GET /api/billing/subscription",
        "description": "Get current subscription details for the authenticated user.",
        "tags": [
          "billing"
        ],
        "responses": {
          "200": {
            "description": "Subscription details retrieved successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "data": {
                      "type": "object",
                      "properties": {
                        "tier": {
                          "type": "string"
                        },
                        "subscriptionStatus": {
                          "type": "string",
                          "nullable": true
                        },
                        "tierName": {
                          "type": "string"
                        },
                        "tierId": {
                          "type": "string"
                        },
                        "stripeCancelAtPeriodEnd": {
                          "type": "boolean"
                        },
                        "stripeCurrentPeriodEnd": {
                          "type": "string",
                          "nullable": true
                        },
                        "stripeCancelAt": {
                          "type": "string",
                          "nullable": true
                        },
                        "trialEndsAt": {
                          "type": "string",
                          "nullable": true
                        },
                        "freeMode": {
                          "type": "boolean"
                        }
                      },
                      "required": [
                        "tier",
                        "subscriptionStatus",
                        "tierName",
                        "tierId",
                        "stripeCancelAtPeriodEnd",
                        "stripeCurrentPeriodEnd",
                        "stripeCancelAt",
                        "trialEndsAt",
                        "freeMode"
                      ]
                    }
                  },
                  "required": [
                    "data"
                  ]
                }
              }
            }
          },
          "404": {
            "description": "User not found."
          }
        }
      },
      "post": {
        "summary": "POST /api/billing/subscription",
        "description": "Create a new subscription checkout session.",
        "tags": [
          "billing"
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "tierId": {
                    "type": "string"
                  }
                },
                "required": [
                  "tierId"
                ]
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "Checkout session created.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "url": {
                      "type": "string"
                    }
                  },
                  "required": [
                    "url"
                  ]
                }
              }
            }
          },
          "400": {
            "description": "Invalid tier."
          },
          "404": {
            "description": "User not found."
          }
        }
      },
      "patch": {
        "summary": "PATCH /api/billing/subscription",
        "description": "Update or cancel an existing subscription.",
        "tags": [
          "billing"
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "action": {
                    "type": "string",
                    "enum": [
                      "cancel",
                      "resume",
                      "change_tier"
                    ]
                  },
                  "tierId": {
                    "type": "string"
                  }
                },
                "required": [
                  "action"
                ]
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "Subscription updated successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "success": {
                      "type": "boolean"
                    },
                    "url": {
                      "type": "string"
                    }
                  },
                  "required": [
                    "success"
                  ]
                }
              }
            }
          },
          "400": {
            "description": "Invalid action or parameters."
          },
          "404": {
            "description": "No active subscription found."
          }
        }
      }
    },
    "/api/billing/subscribe-new": {
      "post": {
        "summary": "POST /api/billing/subscribe-new",
        "description": "Create a checkout session for a new subscription (Pro tier).",
        "tags": [
          "billing"
        ],
        "responses": {
          "200": {
            "description": "Checkout session created.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "data": {
                      "type": "object",
                      "properties": {
                        "url": {
                          "type": "string"
                        }
                      },
                      "required": [
                        "url"
                      ]
                    }
                  },
                  "required": [
                    "data"
                  ]
                }
              }
            }
          },
          "404": {
            "description": "User not found."
          },
          "500": {
            "description": "Internal error."
          }
        }
      }
    },
    "/api/billing/portal": {
      "post": {
        "summary": "POST /api/billing/portal",
        "description": "Create a Stripe billing portal session.",
        "tags": [
          "billing"
        ],
        "responses": {
          "200": {
            "description": "Portal session created.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "url": {
                      "type": "string"
                    }
                  },
                  "required": [
                    "url"
                  ]
                }
              }
            }
          },
          "404": {
            "description": "User or customer not found."
          },
          "500": {
            "description": "Internal error."
          }
        }
      }
    },
    "/api/auth/sync-user": {
      "post": {
        "summary": "POST /api/auth/sync-user",
        "description": "Synchronize user data (e.g., after login) and trigger analytics identify.",
        "tags": [
          "auth"
        ],
        "responses": {
          "200": {
            "description": "User synchronized successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "message": {
                      "type": "string"
                    },
                    "userId": {
                      "type": "string"
                    }
                  },
                  "required": [
                    "message",
                    "userId"
                  ]
                }
              }
            }
          }
        }
      }
    },
    "/api/auth/signout": {
      "post": {
        "summary": "POST /api/auth/signout",
        "description": "Sign out the current user.",
        "tags": [
          "auth"
        ],
        "responses": {
          "200": {
            "description": "Logout successful.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "message": {
                      "type": "string"
                    }
                  },
                  "required": [
                    "message"
                  ]
                }
              }
            }
          }
        }
      }
    },
    "/api/auth/register": {
      "post": {
        "summary": "POST /api/auth/register",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "auth"
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "name": {
                    "type": "string",
                    "minLength": 2
                  },
                  "email": {
                    "type": "string",
                    "format": "email"
                  },
                  "password": {
                    "type": "string",
                    "minLength": 8
                  },
                  "role": {
                    "type": "string",
                    "enum": [
                      "client",
                      "trainer"
                    ]
                  },
                  "redirect": {
                    "type": "string",
                    "format": "url"
                  }
                },
                "required": [
                  "name",
                  "email",
                  "password",
                  "role"
                ]
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "200 response."
          },
          "201": {
            "description": "201 response."
          },
          "400": {
            "description": "400 response."
          },
          "409": {
            "description": "409 response."
          },
          "422": {
            "description": "422 response."
          },
          "502": {
            "description": "502 response."
          }
        }
      }
    },
    "/api/auth/me": {
      "get": {
        "summary": "GET /api/auth/me",
        "description": "Retrieve the current authenticated user's details.",
        "tags": [
          "auth"
        ],
        "responses": {
          "200": {
            "description": "User details retrieved successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "id": {
                      "type": "string"
                    },
                    "email": {
                      "type": "string",
                      "format": "email"
                    },
                    "name": {
                      "type": "string",
                      "nullable": true
                    },
                    "role": {
                      "type": "string"
                    },
                    "username": {
                      "type": "string",
                      "nullable": true
                    },
                    "tier": {
                      "type": "string"
                    },
                    "metadata": {}
                  },
                  "required": [
                    "id",
                    "email",
                    "name",
                    "role",
                    "username",
                    "tier",
                    "metadata"
                  ]
                }
              }
            }
          },
          "401": {
            "description": "Unauthorized."
          }
        }
      }
    },
    "/api/auth/login": {
      "post": {
        "summary": "POST /api/auth/login",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "auth"
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "email": {
                    "type": "string",
                    "format": "email"
                  },
                  "password": {
                    "type": "string",
                    "minLength": 1
                  }
                },
                "required": [
                  "email",
                  "password"
                ]
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "200 response."
          },
          "400": {
            "description": "400 response."
          },
          "401": {
            "description": "401 response."
          },
          "422": {
            "description": "422 response."
          },
          "500": {
            "description": "500 response."
          }
        }
      }
    },
    "/api/auth/complete-onboarding": {
      "post": {
        "summary": "POST /api/auth/complete-onboarding",
        "description": "Mark the user's onboarding as complete.",
        "tags": [
          "auth"
        ],
        "responses": {
          "200": {
            "description": "Onboarding completed.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "success": {
                      "type": "boolean"
                    }
                  },
                  "required": [
                    "success"
                  ]
                }
              }
            }
          }
        }
      }
    },
    "/api/workout-sessions/{id}/summary": {
      "get": {
        "summary": "GET /api/workout-sessions/{id}/summary",
        "description": "Get summary of a completed workout session (authenticated).",
        "tags": [
          "workout-sessions"
        ],
        "parameters": [
          {
            "name": "id",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Summary retrieved successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "session": {},
                    "totalWorkouts": {
                      "type": "number",
                      "example": 0
                    },
                    "bestSet": {
                      "type": "object",
                      "properties": {
                        "exerciseName": {
                          "type": "string",
                          "nullable": true
                        },
                        "reps": {
                          "type": "number",
                          "example": 0
                        },
                        "weight": {
                          "type": "number",
                          "example": 0
                        }
                      },
                      "required": [
                        "exerciseName",
                        "reps",
                        "weight"
                      ]
                    },
                    "newRecordsCount": {
                      "type": "number",
                      "example": 0
                    }
                  },
                  "required": [
                    "session",
                    "totalWorkouts",
                    "bestSet",
                    "newRecordsCount"
                  ]
                }
              }
            }
          },
          "403": {
            "description": "Unauthorized."
          },
          "404": {
            "description": "Session not found."
          }
        }
      }
    },
    "/api/workout-sessions/{id}/save-as-template": {
      "post": {
        "summary": "POST /api/workout-sessions/{id}/save-as-template",
        "description": "Save a completed workout session as a new program template.",
        "tags": [
          "workout-sessions"
        ],
        "parameters": [
          {
            "name": "id",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "templateName": {
                    "type": "string",
                    "minLength": 1
                  }
                },
                "required": [
                  "templateName"
                ]
              }
            }
          }
        },
        "responses": {
          "201": {
            "description": "Template created successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "message": {
                      "type": "string"
                    }
                  },
                  "required": [
                    "message"
                  ]
                }
              }
            }
          },
          "400": {
            "description": "Invalid input."
          },
          "403": {
            "description": "Unauthorized."
          }
        }
      }
    },
    "/api/workout-sessions/{id}/comments": {
      "post": {
        "summary": "POST /api/workout-sessions/{id}/comments",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "workout-sessions"
        ],
        "parameters": [
          {
            "name": "id",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "commentText": {
                    "type": "string",
                    "minLength": 1
                  }
                },
                "required": [
                  "commentText"
                ]
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "200 response."
          },
          "201": {
            "description": "201 response."
          },
          "400": {
            "description": "400 response."
          },
          "422": {
            "description": "422 response."
          }
        }
      }
    },
    "/api/workout-sessions/{id}/cancel": {
      "post": {
        "summary": "POST /api/workout-sessions/{id}/cancel",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "workout-sessions"
        ],
        "parameters": [
          {
            "name": "id",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "200 response."
          }
        }
      }
    },
    "/api/workout/session/active": {
      "get": {
        "summary": "GET /api/workout/session/active",
        "description": "Get the currently active workout session for the authenticated user (Alias).",
        "tags": [
          "workout"
        ],
        "responses": {
          "200": {
            "description": "Active session retrieved successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "session": {
                      "nullable": true
                    }
                  },
                  "required": [
                    "session"
                  ]
                }
              }
            }
          }
        }
      }
    },
    "/api/trainers/{username}/transformation-photos": {
      "get": {
        "summary": "GET /api/trainers/{username}/transformation-photos",
        "description": "Get transformation photos for a trainer.",
        "tags": [
          "trainers"
        ],
        "parameters": [
          {
            "name": "username",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Photos retrieved successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "photos": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "id": {
                            "type": "string"
                          },
                          "imagePath": {
                            "type": "string"
                          },
                          "caption": {
                            "type": "string",
                            "nullable": true
                          },
                          "clientName": {
                            "type": "string",
                            "nullable": true
                          },
                          "publicUrl": {
                            "type": "string"
                          }
                        },
                        "required": [
                          "id",
                          "imagePath",
                          "caption",
                          "clientName",
                          "publicUrl"
                        ]
                      }
                    }
                  },
                  "required": [
                    "photos"
                  ]
                }
              }
            }
          }
        }
      }
    },
    "/api/trainers/{username}/testimonials": {
      "get": {
        "summary": "GET /api/trainers/{username}/testimonials",
        "description": "Get testimonials for a trainer.",
        "tags": [
          "trainers"
        ],
        "parameters": [
          {
            "name": "username",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Testimonials retrieved successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "testimonials": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "id": {
                            "type": "string"
                          },
                          "clientName": {
                            "type": "string"
                          },
                          "testimonialText": {
                            "type": "string"
                          },
                          "rating": {
                            "type": "number",
                            "example": 0,
                            "nullable": true
                          }
                        },
                        "required": [
                          "id",
                          "clientName",
                          "testimonialText",
                          "rating"
                        ]
                      }
                    }
                  },
                  "required": [
                    "testimonials"
                  ]
                }
              }
            }
          }
        }
      }
    },
    "/api/trainers/{username}/schedule": {
      "get": {
        "summary": "GET /api/trainers/{username}/schedule",
        "description": "Get public schedule for a trainer.",
        "tags": [
          "trainers"
        ],
        "parameters": [
          {
            "name": "username",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Schedule retrieved successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "availability": {},
                    "bookings": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "startTime": {
                            "type": "string"
                          },
                          "endTime": {
                            "type": "string"
                          }
                        },
                        "required": [
                          "startTime",
                          "endTime"
                        ]
                      }
                    }
                  },
                  "required": [
                    "availability",
                    "bookings"
                  ]
                }
              }
            }
          },
          "404": {
            "description": "Trainer not found."
          }
        }
      }
    },
    "/api/trainers/{username}/public": {
      "get": {
        "summary": "GET /api/trainers/{id}/public",
        "description": "Retrieve public profile details for a specific trainer.",
        "tags": [
          "Trainers"
        ],
        "parameters": [
          {
            "name": "username",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Successfully retrieved trainer public details.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "id": {
                      "type": "string"
                    },
                    "name": {
                      "type": "string"
                    },
                    "username": {
                      "type": "string",
                      "nullable": true
                    },
                    "role": {
                      "type": "string"
                    },
                    "profile": {
                      "type": "object",
                      "properties": {
                        "bio": {
                          "type": "object",
                          "properties": {
                            "aboutMe": {
                              "type": "string",
                              "nullable": true
                            },
                            "philosophy": {
                              "type": "string",
                              "nullable": true
                            },
                            "methodology": {
                              "type": "string",
                              "nullable": true
                            },
                            "branding": {
                              "type": "string",
                              "nullable": true
                            }
                          },
                          "required": [
                            "aboutMe",
                            "philosophy",
                            "methodology",
                            "branding"
                          ]
                        },
                        "images": {
                          "type": "object",
                          "properties": {
                            "profilePhoto": {
                              "type": "string",
                              "nullable": true
                            },
                            "bannerImage": {
                              "type": "string",
                              "nullable": true
                            }
                          },
                          "required": [
                            "profilePhoto",
                            "bannerImage"
                          ]
                        },
                        "professional": {
                          "type": "object",
                          "properties": {
                            "specialties": {
                              "type": "array",
                              "items": {
                                "type": "string"
                              }
                            },
                            "trainingTypes": {
                              "type": "array",
                              "items": {
                                "type": "string",
                                "enum": [
                                  "IN_PERSON",
                                  "ONLINE"
                                ]
                              }
                            },
                            "certifications": {
                              "type": "string",
                              "nullable": true
                            },
                            "averageRating": {
                              "type": "number",
                              "example": 0,
                              "nullable": true
                            },
                            "minServicePrice": {
                              "type": "number",
                              "example": 0,
                              "nullable": true
                            }
                          },
                          "required": [
                            "specialties",
                            "trainingTypes",
                            "certifications",
                            "averageRating",
                            "minServicePrice"
                          ]
                        },
                        "locations": {
                          "type": "array",
                          "items": {
                            "type": "object",
                            "properties": {
                              "id": {
                                "type": "string"
                              },
                              "address": {
                                "type": "string"
                              },
                              "latitude": {
                                "type": "number",
                                "example": 0,
                                "nullable": true
                              },
                              "longitude": {
                                "type": "number",
                                "example": 0,
                                "nullable": true
                              }
                            },
                            "required": [
                              "id",
                              "address",
                              "latitude",
                              "longitude"
                            ]
                          }
                        },
                        "services": {
                          "type": "array",
                          "items": {
                            "type": "object",
                            "properties": {
                              "id": {
                                "type": "string"
                              },
                              "title": {
                                "type": "string"
                              },
                              "description": {
                                "type": "string"
                              },
                              "price": {
                                "type": "string",
                                "nullable": true
                              },
                              "currency": {
                                "type": "string",
                                "nullable": true
                              },
                              "duration": {
                                "type": "number",
                                "example": 0,
                                "nullable": true
                              }
                            },
                            "required": [
                              "id",
                              "title",
                              "description",
                              "price",
                              "currency",
                              "duration"
                            ]
                          }
                        },
                        "testimonials": {
                          "type": "array",
                          "items": {
                            "type": "object",
                            "properties": {
                              "id": {
                                "type": "string"
                              },
                              "clientName": {
                                "type": "string"
                              },
                              "text": {
                                "type": "string"
                              },
                              "rating": {
                                "type": "number",
                                "example": 0,
                                "nullable": true
                              }
                            },
                            "required": [
                              "id",
                              "clientName",
                              "text",
                              "rating"
                            ]
                          }
                        },
                        "transformations": {
                          "type": "array",
                          "items": {
                            "type": "object",
                            "properties": {
                              "id": {
                                "type": "string"
                              },
                              "imagePath": {
                                "type": "string"
                              },
                              "caption": {
                                "type": "string",
                                "nullable": true
                              },
                              "clientName": {
                                "type": "string",
                                "nullable": true
                              }
                            },
                            "required": [
                              "id",
                              "imagePath",
                              "caption",
                              "clientName"
                            ]
                          }
                        },
                        "socials": {
                          "type": "array",
                          "items": {
                            "type": "object",
                            "properties": {
                              "platform": {
                                "type": "string"
                              },
                              "username": {
                                "type": "string"
                              },
                              "url": {
                                "type": "string"
                              }
                            },
                            "required": [
                              "platform",
                              "username",
                              "url"
                            ]
                          }
                        },
                        "benefits": {
                          "type": "array",
                          "items": {
                            "type": "object",
                            "properties": {
                              "title": {
                                "type": "string"
                              },
                              "description": {
                                "type": "string",
                                "nullable": true
                              },
                              "iconName": {
                                "type": "string",
                                "nullable": true
                              }
                            },
                            "required": [
                              "title",
                              "description",
                              "iconName"
                            ]
                          }
                        }
                      },
                      "required": [
                        "bio",
                        "images",
                        "professional",
                        "locations",
                        "services",
                        "testimonials",
                        "transformations",
                        "socials",
                        "benefits"
                      ],
                      "nullable": true
                    }
                  },
                  "required": [
                    "id",
                    "name",
                    "username",
                    "role",
                    "profile"
                  ]
                }
              }
            }
          },
          "400": {
            "description": "Trainer ID is required."
          },
          "404": {
            "description": "Trainer not found."
          }
        }
      }
    },
    "/api/trainers/{username}/packages": {
      "get": {
        "summary": "GET /api/trainers/{username}/packages",
        "description": "Get packages for a specific trainer by username.",
        "tags": [
          "trainers"
        ],
        "parameters": [
          {
            "name": "username",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Packages retrieved successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "packages": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "id": {
                            "type": "string"
                          },
                          "name": {
                            "type": "string"
                          },
                          "description": {
                            "type": "string",
                            "nullable": true
                          },
                          "price": {
                            "type": "string"
                          },
                          "numberOfSessions": {
                            "type": "number",
                            "example": 0
                          }
                        },
                        "required": [
                          "id",
                          "name",
                          "description",
                          "price",
                          "numberOfSessions"
                        ]
                      }
                    }
                  },
                  "required": [
                    "packages"
                  ]
                }
              }
            }
          },
          "400": {
            "description": "Username required."
          }
        }
      }
    },
    "/api/trainer/programs/templates": {
      "post": {
        "summary": "POST /api/trainer/programs/templates",
        "description": "Create a new workout template.",
        "tags": [
          "trainer"
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "name": {
                    "type": "string",
                    "minLength": 1
                  },
                  "description": {
                    "type": "string",
                    "nullable": true
                  },
                  "programId": {
                    "type": "string"
                  }
                },
                "required": [
                  "name",
                  "programId"
                ]
              }
            }
          }
        },
        "responses": {
          "201": {
            "description": "Template created successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "template": {}
                  },
                  "required": [
                    "template"
                  ]
                }
              }
            }
          },
          "422": {
            "description": "Validation failed."
          }
        }
      },
      "get": {
        "summary": "GET /api/trainer/programs/templates",
        "description": "Get a flat list of templates available for the user.",
        "tags": [
          "trainer"
        ],
        "responses": {
          "200": {
            "description": "Templates retrieved successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "templates": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "id": {
                            "type": "string"
                          },
                          "name": {
                            "type": "string"
                          },
                          "source": {
                            "type": "string",
                            "enum": [
                              "trainer",
                              "system"
                            ]
                          }
                        },
                        "required": [
                          "id",
                          "name"
                        ]
                      }
                    }
                  },
                  "required": [
                    "templates"
                  ]
                }
              }
            }
          }
        }
      }
    },
    "/api/trainer/clients/{id}": {
      "get": {
        "summary": "GET /api/trainer/clients/{id}",
        "description": "Retrieve detailed information for a specific client (Alias).",
        "tags": [
          "trainer"
        ],
        "parameters": [
          {
            "name": "id",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Successfully retrieved client information.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "client": {
                      "type": "object",
                      "properties": {
                        "id": {
                          "type": "string"
                        },
                        "name": {
                          "type": "string"
                        },
                        "email": {
                          "type": "string",
                          "nullable": true
                        },
                        "phone": {
                          "type": "string",
                          "nullable": true
                        },
                        "status": {
                          "type": "string",
                          "enum": [
                            "active",
                            "inactive",
                            "pending"
                          ]
                        },
                        "createdAt": {
                          "type": "string"
                        },
                        "checkInDay": {
                          "type": "number",
                          "example": 0,
                          "nullable": true
                        },
                        "checkInHour": {
                          "type": "number",
                          "example": 0,
                          "nullable": true
                        },
                        "trainerId": {
                          "type": "string"
                        }
                      },
                      "required": [
                        "id",
                        "name",
                        "email",
                        "phone",
                        "status",
                        "createdAt",
                        "checkInDay",
                        "checkInHour",
                        "trainerId"
                      ]
                    }
                  },
                  "required": [
                    "client"
                  ]
                }
              }
            }
          },
          "404": {
            "description": "Client not found."
          }
        }
      },
      "put": {
        "summary": "PUT /api/trainer/clients/{id}",
        "description": "Update client information (Alias).",
        "tags": [
          "trainer"
        ],
        "parameters": [
          {
            "name": "id",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "name": {
                    "type": "string",
                    "minLength": 1
                  },
                  "email": {
                    "type": "string",
                    "format": "email"
                  },
                  "phone": {
                    "type": "string",
                    "minLength": 1
                  },
                  "status": {
                    "type": "string",
                    "enum": [
                      "active",
                      "inactive",
                      "pending"
                    ]
                  },
                  "checkInDay": {
                    "type": "number",
                    "example": 0,
                    "nullable": true
                  },
                  "checkInHour": {
                    "type": "number",
                    "example": 0,
                    "nullable": true
                  }
                }
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "Successfully updated client information."
          }
        }
      },
      "delete": {
        "summary": "DELETE /api/trainer/clients/{id}",
        "description": "Delete a client (Alias).",
        "tags": [
          "trainer"
        ],
        "parameters": [
          {
            "name": "id",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Successfully deleted client."
          }
        }
      }
    },
    "/api/trainer/check-ins/pending": {
      "get": {
        "summary": "GET /api/trainer/check-ins/pending",
        "description": "List all pending check-ins for the trainer.",
        "tags": [
          "trainer"
        ],
        "responses": {
          "200": {
            "description": "Pending check-ins retrieved successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "array",
                  "items": {
                    "type": "object",
                    "properties": {
                      "id": {
                        "type": "string"
                      },
                      "status": {
                        "type": "string",
                        "enum": [
                          "SUBMITTED"
                        ]
                      },
                      "client": {
                        "type": "object",
                        "properties": {
                          "name": {
                            "type": "string"
                          }
                        },
                        "required": [
                          "name"
                        ]
                      },
                      "date": {
                        "type": "string"
                      }
                    },
                    "required": [
                      "id",
                      "status",
                      "client",
                      "date"
                    ]
                  }
                }
              }
            }
          }
        }
      }
    },
    "/api/trainer/check-ins/{id}": {
      "get": {
        "summary": "GET /api/trainer/check-ins/{id}",
        "description": "Get details of a specific check-in.",
        "tags": [
          "trainer"
        ],
        "parameters": [
          {
            "name": "id",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Check-in details retrieved successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "current": {
                      "type": "object",
                      "properties": {
                        "id": {
                          "type": "string"
                        },
                        "status": {
                          "type": "string"
                        },
                        "date": {
                          "type": "string"
                        },
                        "client": {
                          "type": "object",
                          "properties": {
                            "id": {
                              "type": "string"
                            },
                            "name": {
                              "type": "string"
                            }
                          },
                          "required": [
                            "id",
                            "name"
                          ]
                        }
                      },
                      "required": [
                        "id",
                        "status",
                        "date",
                        "client"
                      ]
                    },
                    "previous": {
                      "type": "object",
                      "properties": {
                        "id": {
                          "type": "string"
                        },
                        "status": {
                          "type": "string"
                        },
                        "date": {
                          "type": "string"
                        },
                        "client": {
                          "type": "object",
                          "properties": {
                            "id": {
                              "type": "string"
                            },
                            "name": {
                              "type": "string"
                            }
                          },
                          "required": [
                            "id",
                            "name"
                          ]
                        }
                      },
                      "required": [
                        "id",
                        "status",
                        "date",
                        "client"
                      ],
                      "nullable": true
                    },
                    "history": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "id": {
                            "type": "string"
                          },
                          "status": {
                            "type": "string"
                          },
                          "date": {
                            "type": "string"
                          },
                          "client": {
                            "type": "object",
                            "properties": {
                              "id": {
                                "type": "string"
                              },
                              "name": {
                                "type": "string"
                              }
                            },
                            "required": [
                              "id",
                              "name"
                            ]
                          }
                        },
                        "required": [
                          "id",
                          "status",
                          "date",
                          "client"
                        ]
                      }
                    }
                  },
                  "required": [
                    "current",
                    "previous",
                    "history"
                  ]
                }
              }
            }
          },
          "404": {
            "description": "Check-in not found."
          }
        }
      }
    },
    "/api/trainer/calendar/{sessionId}": {
      "put": {
        "summary": "PUT /api/trainer/calendar/{sessionId}",
        "description": "Update a planned session in the calendar.",
        "tags": [
          "trainer"
        ],
        "parameters": [
          {
            "name": "sessionId",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "startTime": {
                    "type": "string",
                    "format": "datetime"
                  },
                  "endTime": {
                    "type": "string",
                    "format": "datetime"
                  },
                  "notes": {
                    "type": "string",
                    "nullable": true
                  },
                  "templateId": {
                    "type": "string",
                    "nullable": true
                  }
                },
                "required": [
                  "startTime",
                  "endTime"
                ]
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "Session updated successfully."
          },
          "404": {
            "description": "Session not found."
          },
          "409": {
            "description": "Conflict detected."
          }
        }
      },
      "delete": {
        "summary": "DELETE /api/trainer/calendar/{sessionId}",
        "description": "Delete (cancel) a planned session.",
        "tags": [
          "trainer"
        ],
        "parameters": [
          {
            "name": "sessionId",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Session deleted successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "deletedId": {
                      "type": "string"
                    }
                  },
                  "required": [
                    "deletedId"
                  ]
                }
              }
            }
          },
          "404": {
            "description": "Session not found."
          }
        }
      }
    },
    "/api/trainer/calendar/clients-summary": {
      "get": {
        "summary": "Get calendar clients summary",
        "description": "Lightweight endpoint for populating Calendar Week/Month views. Returns ONLY the minimum client identity data needed for rendering \"WhatsApp-style\" overlapping client circles on calendar grid cells. This endpoint is highly optimized and does NOT return full session details (notes, status, templates, workout logs). For complete session information, use the '/api/trainer/calendar' endpoint when the user taps on a specific day.",
        "tags": [
          "Calendar",
          "Trainer"
        ],
        "parameters": [
          {
            "name": "startDate",
            "in": "query",
            "required": true,
            "schema": {
              "type": "string"
            }
          },
          {
            "name": "endDate",
            "in": "query",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Successfully retrieved client summary for the date range",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "data": {
                      "type": "object",
                      "properties": {
                        "summary": {
                          "type": "array",
                          "items": {
                            "type": "object",
                            "properties": {
                              "date": {
                                "type": "string"
                              },
                              "clientId": {
                                "type": "string"
                              },
                              "clientFirstName": {
                                "type": "string"
                              },
                              "clientLastName": {
                                "type": "string"
                              },
                              "clientAvatarUrl": {
                                "type": "string",
                                "nullable": true
                              }
                            },
                            "required": [
                              "date",
                              "clientId",
                              "clientFirstName",
                              "clientLastName",
                              "clientAvatarUrl"
                            ]
                          }
                        }
                      },
                      "required": [
                        "summary"
                      ]
                    }
                  },
                  "required": [
                    "data"
                  ]
                }
              }
            }
          },
          "400": {
            "description": "Bad request - missing or invalid date parameters"
          },
          "401": {
            "description": "Unauthorized - valid trainer authentication required"
          }
        }
      }
    },
    "/api/public/workout-summary/{sessionId}": {
      "get": {
        "summary": "GET /api/public/workout-summary/{sessionId}",
        "description": "Get a public summary of a completed workout session.",
        "tags": [
          "public"
        ],
        "parameters": [
          {
            "name": "sessionId",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Workout summary retrieved successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "session": {
                      "type": "object",
                      "properties": {
                        "id": {
                          "type": "string"
                        },
                        "client": {
                          "type": "object",
                          "properties": {
                            "name": {
                              "type": "string"
                            },
                            "trainer": {
                              "type": "object",
                              "properties": {
                                "name": {
                                  "type": "string",
                                  "nullable": true
                                }
                              },
                              "required": [
                                "name"
                              ],
                              "nullable": true
                            }
                          },
                          "required": [
                            "name",
                            "trainer"
                          ]
                        },
                        "exerciseLogs": {
                          "type": "array",
                          "items": {}
                        },
                        "personalRecords": {
                          "type": "array",
                          "items": {}
                        },
                        "startTime": {
                          "type": "string"
                        },
                        "endTime": {
                          "type": "string",
                          "nullable": true
                        }
                      },
                      "required": [
                        "id",
                        "client",
                        "exerciseLogs",
                        "personalRecords",
                        "startTime",
                        "endTime"
                      ]
                    }
                  },
                  "required": [
                    "session"
                  ]
                }
              }
            }
          },
          "404": {
            "description": "Session not found or not completed."
          }
        }
      }
    },
    "/api/profile/me/transformation-photos": {
      "post": {
        "summary": "POST /api/profile/me/transformation-photos",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "profile"
        ],
        "requestBody": {
          "required": true,
          "content": {
            "multipart/form-data": {
              "schema": {
                "type": "object",
                "properties": {
                  "photoFile": {
                    "type": "string",
                    "format": "binary"
                  },
                  "caption": {
                    "type": "string",
                    "nullable": true
                  },
                  "clientName": {
                    "type": "string",
                    "nullable": true
                  }
                },
                "required": [
                  "photoFile"
                ],
                "additionalProperties": true
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "200 response."
          },
          "201": {
            "description": "201 response."
          },
          "400": {
            "description": "400 response."
          },
          "404": {
            "description": "404 response."
          },
          "500": {
            "description": "500 response."
          }
        }
      },
      "get": {
        "summary": "GET /api/profile/me/transformation-photos",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "profile"
        ],
        "responses": {
          "200": {
            "description": "Successful response."
          }
        }
      }
    },
    "/api/profile/me/text-content": {
      "get": {
        "summary": "GET /api/profile/me/text-content",
        "description": "Get profile text content.",
        "tags": [
          "profile"
        ],
        "responses": {
          "200": {
            "description": "Text content retrieved successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "textContents": {
                      "type": "object",
                      "properties": {
                        "aboutMe": {
                          "type": "string",
                          "nullable": true
                        },
                        "philosophy": {
                          "type": "string",
                          "nullable": true
                        },
                        "methodology": {
                          "type": "string",
                          "nullable": true
                        },
                        "branding": {
                          "type": "string",
                          "nullable": true
                        }
                      },
                      "required": [
                        "aboutMe",
                        "philosophy",
                        "methodology",
                        "branding"
                      ]
                    }
                  },
                  "required": [
                    "textContents"
                  ]
                }
              }
            }
          }
        }
      },
      "put": {
        "summary": "PUT /api/profile/me/text-content",
        "description": "Update a specific profile text field.",
        "tags": [
          "profile"
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "fieldName": {
                    "type": "string",
                    "enum": [
                      "aboutMe",
                      "philosophy",
                      "methodology",
                      "branding"
                    ]
                  },
                  "content": {
                    "type": "string"
                  }
                },
                "required": [
                  "fieldName",
                  "content"
                ]
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "Content updated successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "message": {
                      "type": "string"
                    }
                  },
                  "required": [
                    "message"
                  ]
                }
              }
            }
          },
          "400": {
            "description": "Invalid field name."
          }
        }
      }
    },
    "/api/profile/me/testimonials": {
      "get": {
        "summary": "GET /api/profile/me/testimonials",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "profile"
        ],
        "responses": {
          "200": {
            "description": "Successful response."
          }
        }
      },
      "post": {
        "summary": "POST /api/profile/me/testimonials",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "profile"
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "clientName": {
                    "type": "string",
                    "minLength": 2
                  },
                  "testimonialText": {
                    "type": "string",
                    "minLength": 10
                  },
                  "rating": {
                    "type": "number",
                    "example": 0,
                    "nullable": true
                  }
                },
                "required": [
                  "clientName",
                  "testimonialText"
                ]
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "200 response."
          },
          "201": {
            "description": "201 response."
          },
          "400": {
            "description": "400 response."
          },
          "422": {
            "description": "422 response."
          }
        }
      }
    },
    "/api/profile/me/social-links": {
      "get": {
        "summary": "GET /api/profile/me/social-links",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "profile"
        ],
        "responses": {
          "200": {
            "description": "Successful response."
          }
        }
      },
      "post": {
        "summary": "POST /api/profile/me/social-links",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "profile"
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "platform": {
                    "type": "string",
                    "minLength": 1
                  },
                  "username": {
                    "type": "string",
                    "minLength": 1
                  },
                  "profileUrl": {
                    "type": "string",
                    "format": "url"
                  }
                },
                "required": [
                  "platform",
                  "username",
                  "profileUrl"
                ]
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "200 response."
          },
          "201": {
            "description": "201 response."
          },
          "404": {
            "description": "404 response."
          },
          "422": {
            "description": "422 response."
          }
        }
      }
    },
    "/api/profile/me/services": {
      "get": {
        "summary": "GET /api/profile/me/services",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "profile"
        ],
        "responses": {
          "200": {
            "description": "Successful response."
          }
        }
      },
      "post": {
        "summary": "POST /api/profile/me/services",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "profile"
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "title": {
                    "type": "string",
                    "minLength": 1
                  },
                  "description": {
                    "type": "string",
                    "minLength": 1
                  },
                  "price": {
                    "nullable": true
                  },
                  "currency": {
                    "type": "string",
                    "minLength": 1,
                    "nullable": true
                  },
                  "duration": {
                    "nullable": true
                  }
                },
                "required": [
                  "title",
                  "description"
                ]
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "200 response."
          },
          "201": {
            "description": "201 response."
          },
          "400": {
            "description": "400 response."
          },
          "422": {
            "description": "422 response."
          }
        }
      }
    },
    "/api/profile/me/push-token": {
      "post": {
        "summary": "POST /api/profile/me/push-token",
        "description": "Register a push notification token for the current user.",
        "tags": [
          "profile"
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "token": {
                    "type": "string",
                    "minLength": 1
                  }
                },
                "required": [
                  "token"
                ]
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "Token registered successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "message": {
                      "type": "string"
                    }
                  },
                  "required": [
                    "message"
                  ]
                }
              }
            }
          },
          "400": {
            "description": "Invalid body."
          },
          "422": {
            "description": "Validation failed."
          }
        }
      }
    },
    "/api/profile/me/packages": {
      "get": {
        "summary": "GET /api/profile/me/packages",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "profile"
        ],
        "responses": {
          "200": {
            "description": "Successful response."
          }
        }
      },
      "post": {
        "summary": "POST /api/profile/me/packages",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "profile"
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "name": {
                    "type": "string",
                    "minLength": 2
                  },
                  "description": {
                    "type": "string",
                    "nullable": true
                  },
                  "price": {
                    "type": "number",
                    "example": 0
                  },
                  "numberOfSessions": {
                    "type": "number",
                    "example": 0
                  }
                },
                "required": [
                  "name",
                  "price",
                  "numberOfSessions"
                ]
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "200 response."
          },
          "201": {
            "description": "201 response."
          },
          "400": {
            "description": "400 response."
          },
          "422": {
            "description": "422 response."
          }
        }
      }
    },
    "/api/profile/me/external-links": {
      "get": {
        "summary": "GET /api/profile/me/external-links",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "profile"
        ],
        "responses": {
          "200": {
            "description": "Successful response."
          }
        }
      },
      "post": {
        "summary": "POST /api/profile/me/external-links",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "profile"
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "label": {
                    "type": "string",
                    "minLength": 1
                  },
                  "linkUrl": {
                    "type": "string",
                    "format": "url"
                  }
                },
                "required": [
                  "label",
                  "linkUrl"
                ]
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "200 response."
          },
          "201": {
            "description": "201 response."
          },
          "404": {
            "description": "404 response."
          },
          "422": {
            "description": "422 response."
          }
        }
      }
    },
    "/api/profile/me/exercises": {
      "get": {
        "summary": "GET /api/profile/me/exercises",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "profile"
        ],
        "responses": {
          "200": {
            "description": "Successful response."
          }
        }
      },
      "post": {
        "summary": "POST /api/profile/me/exercises",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "profile"
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "name": {
                    "type": "string",
                    "minLength": 2
                  },
                  "muscleGroup": {
                    "type": "string"
                  },
                  "equipment": {
                    "type": "string"
                  },
                  "videoUrl": {}
                },
                "required": [
                  "name",
                  "videoUrl"
                ]
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "200 response."
          },
          "201": {
            "description": "201 response."
          },
          "401": {
            "description": "401 response."
          },
          "403": {
            "description": "403 response."
          },
          "409": {
            "description": "409 response."
          },
          "422": {
            "description": "422 response."
          }
        }
      }
    },
    "/api/profile/me/core-info": {
      "get": {
        "summary": "GET /api/profile/me/core-info",
        "description": "Get core profile information.",
        "tags": [
          "profile"
        ],
        "responses": {
          "200": {
            "description": "Core info retrieved successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "coreInfo": {
                      "type": "object",
                      "properties": {
                        "name": {
                          "type": "string"
                        },
                        "username": {
                          "type": "string"
                        },
                        "weightUnit": {
                          "type": "string",
                          "enum": [
                            "KG",
                            "LB"
                          ],
                          "nullable": true
                        },
                        "certifications": {
                          "type": "string",
                          "nullable": true
                        },
                        "phone": {
                          "type": "string",
                          "nullable": true
                        },
                        "specialties": {
                          "type": "array",
                          "items": {
                            "type": "string"
                          }
                        },
                        "trainingTypes": {
                          "type": "array",
                          "items": {
                            "type": "string"
                          }
                        },
                        "locations": {
                          "type": "array",
                          "items": {
                            "type": "object",
                            "properties": {
                              "id": {
                                "type": "string"
                              },
                              "address": {
                                "type": "string"
                              },
                              "latitude": {
                                "type": "number",
                                "example": 0,
                                "nullable": true
                              },
                              "longitude": {
                                "type": "number",
                                "example": 0,
                                "nullable": true
                              }
                            },
                            "required": [
                              "id",
                              "address",
                              "latitude",
                              "longitude"
                            ]
                          }
                        }
                      },
                      "required": [
                        "name",
                        "username",
                        "weightUnit",
                        "certifications",
                        "phone",
                        "specialties",
                        "trainingTypes",
                        "locations"
                      ]
                    }
                  },
                  "required": [
                    "coreInfo"
                  ]
                }
              }
            }
          }
        }
      },
      "put": {
        "summary": "PUT /api/profile/me/core-info",
        "description": "Update core profile information.",
        "tags": [
          "profile"
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "name": {
                    "type": "string",
                    "minLength": 2
                  },
                  "username": {
                    "type": "string",
                    "minLength": 3
                  },
                  "certifications": {
                    "type": "string",
                    "nullable": true
                  },
                  "phone": {
                    "type": "string",
                    "nullable": true
                  },
                  "specialties": {
                    "type": "array",
                    "items": {
                      "type": "string"
                    }
                  },
                  "trainingTypes": {
                    "type": "array",
                    "items": {
                      "type": "string"
                    }
                  },
                  "weightUnit": {
                    "type": "string",
                    "enum": [
                      "KG",
                      "LB"
                    ]
                  },
                  "locations": {
                    "type": "array",
                    "items": {
                      "type": "string"
                    }
                  }
                },
                "required": [
                  "name",
                  "username"
                ]
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "Core info updated successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "coreInfo": {
                      "type": "object",
                      "properties": {
                        "name": {
                          "type": "string"
                        },
                        "username": {
                          "type": "string"
                        },
                        "weightUnit": {
                          "type": "string",
                          "enum": [
                            "KG",
                            "LB"
                          ],
                          "nullable": true
                        },
                        "certifications": {
                          "type": "string",
                          "nullable": true
                        },
                        "phone": {
                          "type": "string",
                          "nullable": true
                        },
                        "specialties": {
                          "type": "array",
                          "items": {
                            "type": "string"
                          }
                        },
                        "trainingTypes": {
                          "type": "array",
                          "items": {
                            "type": "string"
                          }
                        },
                        "locations": {
                          "type": "array",
                          "items": {
                            "type": "object",
                            "properties": {
                              "id": {
                                "type": "string"
                              },
                              "address": {
                                "type": "string"
                              },
                              "latitude": {
                                "type": "number",
                                "example": 0,
                                "nullable": true
                              },
                              "longitude": {
                                "type": "number",
                                "example": 0,
                                "nullable": true
                              }
                            },
                            "required": [
                              "id",
                              "address",
                              "latitude",
                              "longitude"
                            ]
                          }
                        }
                      },
                      "required": [
                        "name",
                        "username",
                        "weightUnit",
                        "certifications",
                        "phone",
                        "specialties",
                        "trainingTypes",
                        "locations"
                      ]
                    }
                  },
                  "required": [
                    "coreInfo"
                  ]
                }
              }
            }
          },
          "409": {
            "description": "Username taken."
          },
          "422": {
            "description": "Validation failed."
          }
        }
      }
    },
    "/api/profile/me/branding": {
      "get": {
        "summary": "GET /api/profile/me/branding",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "profile"
        ],
        "responses": {
          "200": {
            "description": "Successful response."
          }
        }
      },
      "post": {
        "summary": "POST /api/profile/me/branding",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "profile"
        ],
        "requestBody": {
          "required": true,
          "content": {
            "multipart/form-data": {
              "schema": {
                "type": "object",
                "properties": {
                  "bannerImage": {
                    "type": "string",
                    "format": "binary"
                  },
                  "profileImage": {
                    "type": "string",
                    "format": "binary"
                  }
                },
                "anyOf": [
                  {
                    "required": [
                      "bannerImage"
                    ]
                  },
                  {
                    "required": [
                      "profileImage"
                    ]
                  }
                ],
                "additionalProperties": true
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "200 response."
          },
          "201": {
            "description": "201 response."
          },
          "400": {
            "description": "400 response."
          },
          "403": {
            "description": "403 response."
          },
          "404": {
            "description": "404 response."
          },
          "500": {
            "description": "500 response."
          }
        }
      }
    },
    "/api/profile/me/billing": {
      "get": {
        "summary": "GET /api/profile/me/billing",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "profile"
        ],
        "responses": {
          "200": {
            "description": "Successful response."
          }
        }
      },
      "post": {
        "summary": "POST /api/profile/me/billing",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "profile"
        ],
        "responses": {
          "200": {
            "description": "200 response."
          },
          "404": {
            "description": "404 response."
          }
        }
      }
    },
    "/api/profile/me/benefits": {
      "get": {
        "summary": "GET /api/profile/me/benefits",
        "description": "List profile benefits.",
        "tags": [
          "profile"
        ],
        "responses": {
          "200": {
            "description": "Benefits retrieved successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "benefits": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "id": {
                            "type": "string"
                          },
                          "title": {
                            "type": "string"
                          },
                          "description": {
                            "type": "string",
                            "nullable": true
                          },
                          "orderColumn": {
                            "type": "number",
                            "example": 0
                          }
                        },
                        "required": [
                          "id",
                          "title",
                          "description",
                          "orderColumn"
                        ]
                      }
                    }
                  },
                  "required": [
                    "benefits"
                  ]
                }
              }
            }
          }
        }
      },
      "post": {
        "summary": "POST /api/profile/me/benefits",
        "description": "Add a new benefit to the profile.",
        "tags": [
          "profile"
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "title": {
                    "type": "string",
                    "minLength": 1
                  },
                  "description": {
                    "type": "string",
                    "nullable": true
                  },
                  "iconName": {
                    "type": "string",
                    "nullable": true
                  },
                  "iconStyle": {
                    "type": "string",
                    "nullable": true
                  }
                },
                "required": [
                  "title"
                ]
              }
            }
          }
        },
        "responses": {
          "201": {
            "description": "Benefit added successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "newBenefit": {
                      "type": "object",
                      "properties": {
                        "id": {
                          "type": "string"
                        },
                        "title": {
                          "type": "string"
                        },
                        "description": {
                          "type": "string",
                          "nullable": true
                        },
                        "orderColumn": {
                          "type": "number",
                          "example": 0
                        }
                      },
                      "required": [
                        "id",
                        "title",
                        "description",
                        "orderColumn"
                      ]
                    }
                  },
                  "required": [
                    "newBenefit"
                  ]
                }
              }
            }
          },
          "422": {
            "description": "Validation failed."
          }
        }
      }
    },
    "/api/profile/me/availability": {
      "get": {
        "summary": "GET /api/profile/me/availability",
        "description": "Get trainer availability.",
        "tags": [
          "profile"
        ],
        "responses": {
          "200": {
            "description": "Availability retrieved successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "availability": {}
                  },
                  "required": [
                    "availability"
                  ]
                }
              }
            }
          }
        }
      },
      "put": {
        "summary": "PUT /api/profile/me/availability",
        "description": "Update trainer availability.",
        "tags": [
          "profile"
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "availability": {}
                },
                "required": [
                  "availability"
                ]
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "Availability updated successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "message": {
                      "type": "string"
                    }
                  },
                  "required": [
                    "message"
                  ]
                }
              }
            }
          }
        }
      }
    },
    "/api/profile/me/assessments": {
      "get": {
        "summary": "GET /api/profile/me/assessments",
        "description": "List all assessments available to the user.",
        "tags": [
          "profile"
        ],
        "responses": {
          "200": {
            "description": "Assessments retrieved successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "assessments": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "id": {
                            "type": "string"
                          },
                          "name": {
                            "type": "string"
                          },
                          "description": {
                            "type": "string",
                            "nullable": true
                          },
                          "unit": {
                            "type": "string"
                          },
                          "trainerId": {
                            "type": "string",
                            "nullable": true
                          }
                        },
                        "required": [
                          "id",
                          "name",
                          "description",
                          "unit",
                          "trainerId"
                        ]
                      }
                    }
                  },
                  "required": [
                    "assessments"
                  ]
                }
              }
            }
          }
        }
      },
      "post": {
        "summary": "POST /api/profile/me/assessments",
        "description": "Create a new custom assessment.",
        "tags": [
          "profile"
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "name": {
                    "type": "string",
                    "minLength": 2
                  },
                  "description": {
                    "type": "string"
                  },
                  "unit": {
                    "type": "string",
                    "minLength": 1
                  }
                },
                "required": [
                  "name",
                  "unit"
                ]
              }
            }
          }
        },
        "responses": {
          "201": {
            "description": "Assessment created successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "newAssessment": {
                      "type": "object",
                      "properties": {
                        "id": {
                          "type": "string"
                        },
                        "name": {
                          "type": "string"
                        },
                        "description": {
                          "type": "string",
                          "nullable": true
                        },
                        "unit": {
                          "type": "string"
                        },
                        "trainerId": {
                          "type": "string",
                          "nullable": true
                        }
                      },
                      "required": [
                        "id",
                        "name",
                        "description",
                        "unit",
                        "trainerId"
                      ]
                    }
                  },
                  "required": [
                    "newAssessment"
                  ]
                }
              }
            }
          },
          "409": {
            "description": "Assessment name exists."
          },
          "422": {
            "description": "Validation failed."
          }
        }
      }
    },
    "/api/clients/{id}/sessions": {
      "get": {
        "summary": "GET /api/clients/{id}/sessions",
        "description": "List workout sessions for a client with pagination.",
        "tags": [
          "clients"
        ],
        "parameters": [
          {
            "name": "id",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          },
          {
            "name": "limit",
            "in": "query",
            "required": false,
            "schema": {
              "type": "number",
              "example": 0
            }
          },
          {
            "name": "offset",
            "in": "query",
            "required": false,
            "schema": {
              "type": "number",
              "example": 0
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Successfully retrieved client sessions.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "sessions": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "id": {
                            "type": "string"
                          },
                          "startTime": {
                            "type": "string"
                          },
                          "endTime": {
                            "type": "string",
                            "nullable": true
                          },
                          "status": {
                            "type": "string",
                            "enum": [
                              "IN_PROGRESS",
                              "COMPLETED",
                              "PLANNED",
                              "CANCELLED"
                            ]
                          },
                          "notes": {
                            "type": "string",
                            "nullable": true
                          }
                        },
                        "required": [
                          "id",
                          "startTime",
                          "endTime",
                          "status",
                          "notes"
                        ]
                      }
                    },
                    "totalCount": {
                      "type": "number",
                      "example": 0
                    },
                    "limit": {
                      "type": "number",
                      "example": 0
                    },
                    "offset": {
                      "type": "number",
                      "example": 0
                    }
                  },
                  "required": [
                    "sessions",
                    "totalCount",
                    "limit",
                    "offset"
                  ]
                }
              }
            }
          },
          "400": {
            "description": "Client ID is required."
          },
          "404": {
            "description": "Client not found or unauthorized."
          },
          "422": {
            "description": "Validation failed."
          }
        }
      }
    },
    "/api/clients/{id}/photos": {
      "get": {
        "summary": "GET /api/clients/{id}/photos",
        "description": "List progress photos for a client with pagination.",
        "tags": [
          "clients"
        ],
        "parameters": [
          {
            "name": "id",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          },
          {
            "name": "limit",
            "in": "query",
            "required": false,
            "schema": {
              "type": "number",
              "example": 0
            }
          },
          {
            "name": "offset",
            "in": "query",
            "required": false,
            "schema": {
              "type": "number",
              "example": 0
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Successfully retrieved client photos.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "photos": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "id": {
                            "type": "string"
                          },
                          "clientId": {
                            "type": "string"
                          },
                          "imagePath": {
                            "type": "string"
                          },
                          "photoDate": {
                            "type": "string"
                          },
                          "caption": {
                            "type": "string",
                            "nullable": true
                          },
                          "createdAt": {
                            "type": "string"
                          },
                          "updatedAt": {
                            "type": "string"
                          },
                          "publicUrl": {
                            "type": "string",
                            "format": "url"
                          }
                        },
                        "required": [
                          "id",
                          "clientId",
                          "imagePath",
                          "photoDate",
                          "caption",
                          "createdAt",
                          "updatedAt"
                        ]
                      }
                    },
                    "totalCount": {
                      "type": "number",
                      "example": 0
                    },
                    "limit": {
                      "type": "number",
                      "example": 0
                    },
                    "offset": {
                      "type": "number",
                      "example": 0
                    }
                  },
                  "required": [
                    "photos",
                    "totalCount",
                    "limit",
                    "offset"
                  ]
                }
              }
            }
          },
          "400": {
            "description": "Client ID is required."
          },
          "422": {
            "description": "Validation failed."
          }
        }
      },
      "post": {
        "summary": "POST /api/clients/{id}/photos",
        "description": "Upload a new progress photo for a client.",
        "tags": [
          "clients"
        ],
        "parameters": [
          {
            "name": "id",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "content": {
                  "multipart/form-data": {
                    "schema": {
                      "type": "object",
                      "properties": {
                        "photo": {
                          "type": "string",
                          "format": "binary"
                        },
                        "photoDate": {
                          "type": "string"
                        },
                        "caption": {
                          "type": "string",
                          "nullable": true
                        }
                      },
                      "required": [
                        "photo",
                        "photoDate"
                      ]
                    }
                  }
                }
              }
            }
          }
        },
        "responses": {
          "201": {
            "description": "Successfully uploaded photo.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "progressPhoto": {
                      "type": "object",
                      "properties": {
                        "id": {
                          "type": "string"
                        },
                        "clientId": {
                          "type": "string"
                        },
                        "imagePath": {
                          "type": "string"
                        },
                        "photoDate": {
                          "type": "string"
                        },
                        "caption": {
                          "type": "string",
                          "nullable": true
                        },
                        "createdAt": {
                          "type": "string"
                        },
                        "updatedAt": {
                          "type": "string"
                        },
                        "publicUrl": {
                          "type": "string",
                          "format": "url"
                        }
                      },
                      "required": [
                        "id",
                        "clientId",
                        "imagePath",
                        "photoDate",
                        "caption",
                        "createdAt",
                        "updatedAt"
                      ]
                    }
                  },
                  "required": [
                    "progressPhoto"
                  ]
                }
              }
            }
          },
          "400": {
            "description": "Missing file or invalid input."
          },
          "403": {
            "description": "Not authorized."
          },
          "500": {
            "description": "Upload failed."
          }
        }
      }
    },
    "/api/clients/{id}/packages": {
      "get": {
        "summary": "GET /api/clients/{id}/packages",
        "description": "List packages purchased by the client.",
        "tags": [
          "clients"
        ],
        "parameters": [
          {
            "name": "id",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Successfully retrieved packages.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "packages": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "id": {
                            "type": "string"
                          },
                          "name": {
                            "type": "string"
                          },
                          "sessionCount": {
                            "type": "number",
                            "example": 0
                          },
                          "price": {
                            "type": "number",
                            "example": 0
                          },
                          "currency": {
                            "type": "string"
                          }
                        },
                        "required": [
                          "id",
                          "name",
                          "sessionCount",
                          "price",
                          "currency"
                        ]
                      }
                    }
                  },
                  "required": [
                    "packages"
                  ]
                }
              }
            }
          },
          "400": {
            "description": "Client ID is required."
          }
        }
      }
    },
    "/api/clients/{id}/insights": {
      "post": {
        "summary": "POST /api/clients/{id}/insights",
        "description": "Generate AI-powered insights for a client based on their history.",
        "tags": [
          "clients"
        ],
        "parameters": [
          {
            "name": "id",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Successfully generated insights.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "summary": {
                      "type": "string"
                    },
                    "recommendations": {
                      "type": "array",
                      "items": {
                        "type": "string"
                      }
                    },
                    "analysis": {}
                  },
                  "required": [
                    "summary",
                    "recommendations"
                  ]
                }
              }
            }
          },
          "404": {
            "description": "Client not found."
          },
          "500": {
            "description": "Failed to generate insights."
          }
        }
      }
    },
    "/api/clients/{id}/measurements": {
      "get": {
        "summary": "GET /api/clients/{id}/measurements",
        "description": "List measurements for a client.",
        "tags": [
          "clients"
        ],
        "parameters": [
          {
            "name": "id",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Successfully retrieved measurements.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "measurements": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "id": {
                            "type": "string"
                          },
                          "clientId": {
                            "type": "string"
                          },
                          "measurementDate": {
                            "type": "string"
                          },
                          "weightKg": {
                            "type": "number",
                            "example": 0,
                            "nullable": true
                          },
                          "bodyFatPercentage": {
                            "type": "number",
                            "example": 0,
                            "nullable": true
                          },
                          "notes": {
                            "type": "string",
                            "nullable": true
                          },
                          "createdAt": {
                            "type": "string"
                          },
                          "updatedAt": {
                            "type": "string"
                          }
                        },
                        "required": [
                          "id",
                          "clientId",
                          "measurementDate",
                          "weightKg",
                          "bodyFatPercentage",
                          "notes",
                          "createdAt",
                          "updatedAt"
                        ]
                      }
                    }
                  },
                  "required": [
                    "measurements"
                  ]
                }
              }
            }
          },
          "400": {
            "description": "Client ID is required."
          }
        }
      },
      "post": {
        "summary": "POST /api/clients/{id}/measurements",
        "description": "Create a new measurement record for a client.",
        "tags": [
          "clients"
        ],
        "parameters": [
          {
            "name": "id",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "measurementDate": {
                    "type": "string"
                  },
                  "weightKg": {
                    "type": "number",
                    "example": 0,
                    "nullable": true
                  },
                  "bodyFatPercentage": {
                    "type": "number",
                    "example": 0,
                    "nullable": true
                  },
                  "notes": {
                    "type": "string",
                    "nullable": true
                  },
                  "customMetrics": {
                    "nullable": true
                  }
                },
                "required": [
                  "measurementDate"
                ]
              }
            }
          }
        },
        "responses": {
          "201": {
            "description": "Successfully created measurement.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "measurement": {
                      "type": "object",
                      "properties": {
                        "id": {
                          "type": "string"
                        },
                        "clientId": {
                          "type": "string"
                        },
                        "measurementDate": {
                          "type": "string"
                        },
                        "weightKg": {
                          "type": "number",
                          "example": 0,
                          "nullable": true
                        },
                        "bodyFatPercentage": {
                          "type": "number",
                          "example": 0,
                          "nullable": true
                        },
                        "notes": {
                          "type": "string",
                          "nullable": true
                        },
                        "createdAt": {
                          "type": "string"
                        },
                        "updatedAt": {
                          "type": "string"
                        }
                      },
                      "required": [
                        "id",
                        "clientId",
                        "measurementDate",
                        "weightKg",
                        "bodyFatPercentage",
                        "notes",
                        "createdAt",
                        "updatedAt"
                      ]
                    }
                  },
                  "required": [
                    "measurement"
                  ]
                }
              }
            }
          },
          "400": {
            "description": "Invalid input."
          },
          "422": {
            "description": "Validation failed."
          }
        }
      }
    },
    "/api/clients/{id}/exercise-logs": {
      "post": {
        "summary": "POST /api/clients/{id}/exercise-logs",
        "description": "Create a new exercise log for a client.",
        "tags": [
          "clients"
        ],
        "parameters": [
          {
            "name": "id",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "exerciseId": {
                    "type": "string",
                    "minLength": 1
                  },
                  "logDate": {
                    "type": "string"
                  },
                  "sets": {}
                },
                "required": [
                  "exerciseId",
                  "logDate",
                  "sets"
                ]
              }
            }
          }
        },
        "responses": {
          "201": {
            "description": "Successfully created exercise log.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "newLog": {
                      "type": "object",
                      "properties": {
                        "id": {
                          "type": "string"
                        },
                        "exerciseId": {
                          "type": "string"
                        },
                        "clientId": {
                          "type": "string"
                        },
                        "logDate": {
                          "type": "string"
                        },
                        "sets": {}
                      },
                      "required": [
                        "id",
                        "exerciseId",
                        "clientId",
                        "logDate",
                        "sets"
                      ]
                    }
                  },
                  "required": [
                    "newLog"
                  ]
                }
              }
            }
          },
          "400": {
            "description": "Invalid JSON body or client ID."
          },
          "422": {
            "description": "Validation failed."
          }
        }
      }
    },
    "/api/clients/{id}/dashboard": {
      "get": {
        "summary": "GET /api/clients/{id}/dashboard",
        "description": "Get aggregated dashboard data for a client.",
        "tags": [
          "clients"
        ],
        "parameters": [
          {
            "name": "id",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Successfully retrieved dashboard data.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "recentActivity": {
                      "type": "array",
                      "items": {}
                    },
                    "stats": {
                      "type": "object",
                      "properties": {
                        "totalWorkouts": {
                          "type": "number",
                          "example": 0
                        },
                        "totalVolume": {
                          "type": "number",
                          "example": 0
                        },
                        "activeStreak": {
                          "type": "number",
                          "example": 0
                        }
                      },
                      "required": [
                        "totalWorkouts",
                        "totalVolume",
                        "activeStreak"
                      ]
                    }
                  },
                  "required": [
                    "recentActivity"
                  ]
                }
              }
            }
          },
          "400": {
            "description": "Client ID is required."
          }
        }
      }
    },
    "/api/clients/{id}/avatar": {
      "post": {
        "summary": "POST /api/clients/{id}/avatar",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "clients"
        ],
        "parameters": [
          {
            "name": "id",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Successful response."
          }
        }
      },
      "delete": {
        "summary": "DELETE /api/clients/{id}/avatar",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "clients"
        ],
        "parameters": [
          {
            "name": "id",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Successful response."
          }
        }
      }
    },
    "/api/clients/{id}/assessments": {
      "get": {
        "summary": "GET /api/clients/{id}/assessments",
        "description": "List assessment results for a client.",
        "tags": [
          "clients"
        ],
        "parameters": [
          {
            "name": "id",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Successfully retrieved assessment results.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "assessmentResults": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "id": {
                            "type": "string"
                          },
                          "assessmentId": {
                            "type": "string"
                          },
                          "clientId": {
                            "type": "string"
                          },
                          "date": {
                            "type": "string"
                          },
                          "value": {
                            "type": "number",
                            "example": 0
                          },
                          "notes": {
                            "type": "string",
                            "nullable": true
                          }
                        },
                        "required": [
                          "id",
                          "assessmentId",
                          "clientId",
                          "date",
                          "value",
                          "notes"
                        ]
                      }
                    }
                  },
                  "required": [
                    "assessmentResults"
                  ]
                }
              }
            }
          },
          "400": {
            "description": "Client ID is required."
          }
        }
      },
      "post": {
        "summary": "POST /api/clients/{id}/assessments",
        "description": "Record a new assessment result.",
        "tags": [
          "clients"
        ],
        "parameters": [
          {
            "name": "id",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "assessmentId": {
                    "type": "string"
                  },
                  "date": {
                    "type": "string"
                  },
                  "value": {
                    "type": "number",
                    "example": 0
                  },
                  "notes": {
                    "type": "string"
                  }
                },
                "required": [
                  "assessmentId",
                  "date",
                  "value"
                ]
              }
            }
          }
        },
        "responses": {
          "201": {
            "description": "Successfully created assessment result.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "assessmentResult": {
                      "type": "object",
                      "properties": {
                        "id": {
                          "type": "string"
                        },
                        "assessmentId": {
                          "type": "string"
                        },
                        "clientId": {
                          "type": "string"
                        },
                        "date": {
                          "type": "string"
                        },
                        "value": {
                          "type": "number",
                          "example": 0
                        },
                        "notes": {
                          "type": "string",
                          "nullable": true
                        }
                      },
                      "required": [
                        "id",
                        "assessmentId",
                        "clientId",
                        "date",
                        "value",
                        "notes"
                      ]
                    }
                  },
                  "required": [
                    "assessmentResult"
                  ]
                }
              }
            }
          },
          "400": {
            "description": "Invalid input."
          },
          "422": {
            "description": "Validation failed."
          }
        }
      }
    },
    "/api/client/trainer/link": {
      "post": {
        "summary": "POST /api/client/trainer/link",
        "description": "Link client account to a trainer.",
        "tags": [
          "client"
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "trainerUsername": {
                    "type": "string"
                  }
                },
                "required": [
                  "trainerUsername"
                ]
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "Successfully linked with trainer.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "message": {
                      "type": "string"
                    }
                  },
                  "required": [
                    "message"
                  ]
                }
              }
            }
          },
          "400": {
            "description": "Trainer username required."
          },
          "401": {
            "description": "Authentication error."
          },
          "404": {
            "description": "Trainer or client profile not found."
          }
        }
      },
      "delete": {
        "summary": "DELETE /api/client/trainer/link",
        "description": "Unlink client account from current trainer.",
        "tags": [
          "client"
        ],
        "responses": {
          "200": {
            "description": "Successfully unlinked from trainer.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "message": {
                      "type": "string"
                    }
                  },
                  "required": [
                    "message"
                  ]
                }
              }
            }
          },
          "400": {
            "description": "Not currently linked to a trainer."
          }
        }
      }
    },
    "/api/client/stats/exercise": {
      "get": {
        "summary": "GET /api/client/stats/exercise",
        "description": "Get performance history for a specific exercise.",
        "tags": [
          "client"
        ],
        "parameters": [
          {
            "name": "exerciseId",
            "in": "query",
            "required": true,
            "schema": {
              "type": "string"
            }
          },
          {
            "name": "metric",
            "in": "query",
            "required": false,
            "schema": {
              "type": "string",
              "enum": [
                "e1rm",
                "volume",
                "bestSetVolume"
              ]
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Exercise stats retrieved successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "exerciseId": {
                      "type": "string"
                    },
                    "metric": {
                      "type": "string"
                    },
                    "history": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "date": {},
                          "value": {
                            "type": "number",
                            "example": 0
                          }
                        },
                        "required": [
                          "date",
                          "value"
                        ]
                      }
                    }
                  },
                  "required": [
                    "exerciseId",
                    "metric",
                    "history"
                  ]
                }
              }
            }
          },
          "400": {
            "description": "Invalid request parameters."
          },
          "404": {
            "description": "Client or exercise not found."
          }
        }
      }
    },
    "/api/client/check-ins/{id}": {
      "get": {
        "summary": "GET /api/client/check-ins/{id}",
        "description": "Get detailed information for a specific check-in.",
        "tags": [
          "client"
        ],
        "parameters": [
          {
            "name": "id",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Detailed check-in information including photos and feedback.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "id": {
                      "type": "string"
                    },
                    "date": {},
                    "status": {
                      "type": "string"
                    },
                    "weight": {
                      "type": "number",
                      "example": 0,
                      "nullable": true
                    },
                    "waistCm": {
                      "type": "number",
                      "example": 0,
                      "nullable": true
                    },
                    "sleepHours": {
                      "type": "number",
                      "example": 0,
                      "nullable": true
                    },
                    "energyLevel": {
                      "type": "number",
                      "example": 0,
                      "nullable": true
                    },
                    "stressLevel": {
                      "type": "number",
                      "example": 0,
                      "nullable": true
                    },
                    "hungerLevel": {
                      "type": "number",
                      "example": 0,
                      "nullable": true
                    },
                    "digestionLevel": {
                      "type": "number",
                      "example": 0,
                      "nullable": true
                    },
                    "nutritionCompliance": {
                      "type": "string",
                      "nullable": true
                    },
                    "clientNotes": {
                      "type": "string",
                      "nullable": true
                    },
                    "trainerResponse": {
                      "type": "string",
                      "nullable": true
                    },
                    "reviewedAt": {
                      "nullable": true
                    },
                    "photos": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "id": {
                            "type": "string"
                          },
                          "imageUrl": {
                            "type": "string"
                          },
                          "caption": {
                            "type": "string",
                            "nullable": true
                          },
                          "photoDate": {}
                        },
                        "required": [
                          "id",
                          "imageUrl",
                          "caption",
                          "photoDate"
                        ]
                      }
                    },
                    "reviewedBy": {
                      "type": "object",
                      "properties": {
                        "name": {
                          "type": "string"
                        },
                        "photoUrl": {
                          "type": "string",
                          "nullable": true
                        }
                      },
                      "required": [
                        "name",
                        "photoUrl"
                      ],
                      "nullable": true
                    }
                  },
                  "required": [
                    "id",
                    "date",
                    "status",
                    "weight",
                    "waistCm",
                    "sleepHours",
                    "energyLevel",
                    "stressLevel",
                    "hungerLevel",
                    "digestionLevel",
                    "nutritionCompliance",
                    "clientNotes",
                    "trainerResponse",
                    "reviewedAt",
                    "photos",
                    "reviewedBy"
                  ]
                }
              }
            }
          },
          "400": {
            "description": "Check-in ID is required."
          },
          "401": {
            "description": "Unauthorized."
          },
          "403": {
            "description": "Forbidden (Check-in belongs to another client)."
          },
          "404": {
            "description": "Check-in not found."
          }
        }
      }
    },
    "/api/client/check-in/config": {
      "get": {
        "summary": "GET /api/client/check-in/config",
        "description": "Get check-in configuration and next due date.",
        "tags": [
          "client"
        ],
        "responses": {
          "200": {
            "description": "Configuration and schedule info.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "checkInDay": {
                      "type": "number",
                      "example": 0
                    },
                    "checkInHour": {
                      "type": "number",
                      "example": 0
                    },
                    "nextCheckInDueAt": {
                      "type": "string",
                      "format": "datetime"
                    }
                  },
                  "required": [
                    "checkInDay",
                    "checkInHour",
                    "nextCheckInDueAt"
                  ]
                }
              }
            }
          },
          "401": {
            "description": "Unauthorized."
          },
          "404": {
            "description": "Client profile not found."
          }
        }
      }
    },
    "/api/bookings/{bookingId}/decline": {
      "put": {
        "summary": "PUT /api/bookings/{bookingId}/decline",
        "description": "Decline (cancel) a pending booking.",
        "tags": [
          "bookings"
        ],
        "parameters": [
          {
            "name": "bookingId",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Booking declined successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "booking": {
                      "type": "object",
                      "properties": {
                        "id": {
                          "type": "string"
                        },
                        "status": {
                          "type": "string",
                          "enum": [
                            "CANCELLED"
                          ]
                        }
                      },
                      "required": [
                        "id",
                        "status"
                      ]
                    }
                  },
                  "required": [
                    "booking"
                  ]
                }
              }
            }
          },
          "404": {
            "description": "Booking not found or not pending."
          }
        }
      }
    },
    "/api/bookings/{bookingId}/confirm": {
      "put": {
        "summary": "PUT /api/bookings/{bookingId}/confirm",
        "description": "Confirm a pending booking.",
        "tags": [
          "bookings"
        ],
        "parameters": [
          {
            "name": "bookingId",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Booking confirmed successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "booking": {
                      "type": "object",
                      "properties": {
                        "id": {
                          "type": "string"
                        },
                        "status": {
                          "type": "string",
                          "enum": [
                            "CONFIRMED"
                          ]
                        }
                      },
                      "required": [
                        "id",
                        "status"
                      ]
                    }
                  },
                  "required": [
                    "booking"
                  ]
                }
              }
            }
          },
          "404": {
            "description": "Booking not found or not pending."
          }
        }
      }
    },
    "/api/workout-sessions/{id}/rest/start": {
      "post": {
        "summary": "POST /api/workout-sessions/{id}/rest/start",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "workout-sessions"
        ],
        "parameters": [
          {
            "name": "id",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "200 response."
          }
        }
      }
    },
    "/api/workout-sessions/{id}/rest/end": {
      "post": {
        "summary": "POST /api/workout-sessions/{id}/rest/end",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "workout-sessions"
        ],
        "parameters": [
          {
            "name": "id",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "200 response."
          },
          "403": {
            "description": "403 response."
          }
        }
      }
    },
    "/api/trainer/check-ins/{id}/review": {
      "patch": {
        "summary": "PATCH /api/trainer/check-ins/{id}/review",
        "description": "Submit a review/response for a client check-in.",
        "tags": [
          "trainer"
        ],
        "parameters": [
          {
            "name": "id",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "trainerResponse": {
                    "type": "string",
                    "minLength": 1
                  }
                },
                "required": [
                  "trainerResponse"
                ]
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "Check-in reviewed successfully."
          },
          "404": {
            "description": "Check-in not found."
          }
        }
      }
    },
    "/api/profile/me/transformation-photos/{photoId}": {
      "delete": {
        "summary": "DELETE /api/profile/me/transformation-photos/{photoId}",
        "description": "Delete a transformation photo.",
        "tags": [
          "profile"
        ],
        "parameters": [
          {
            "name": "photoId",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Photo deleted successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "deletedId": {
                      "type": "string"
                    },
                    "message": {
                      "type": "string"
                    }
                  },
                  "required": [
                    "deletedId",
                    "message"
                  ]
                }
              }
            }
          },
          "404": {
            "description": "Photo not found."
          }
        }
      }
    },
    "/api/profile/me/testimonials/{testimonialId}": {
      "put": {
        "summary": "PUT /api/profile/me/testimonials/{testimonialId}",
        "description": "Update a testimonial.",
        "tags": [
          "profile"
        ],
        "parameters": [
          {
            "name": "testimonialId",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "clientName": {
                    "type": "string",
                    "minLength": 2
                  },
                  "testimonialText": {
                    "type": "string",
                    "minLength": 10
                  },
                  "rating": {
                    "type": "number",
                    "example": 0,
                    "nullable": true
                  }
                },
                "required": [
                  "clientName",
                  "testimonialText"
                ]
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "Testimonial updated successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "testimonial": {
                      "type": "object",
                      "properties": {
                        "id": {
                          "type": "string"
                        },
                        "clientName": {
                          "type": "string"
                        },
                        "testimonialText": {
                          "type": "string"
                        },
                        "rating": {
                          "type": "number",
                          "example": 0,
                          "nullable": true
                        }
                      },
                      "required": [
                        "id",
                        "clientName",
                        "testimonialText",
                        "rating"
                      ]
                    }
                  },
                  "required": [
                    "testimonial"
                  ]
                }
              }
            }
          },
          "404": {
            "description": "Testimonial not found."
          }
        }
      },
      "delete": {
        "summary": "DELETE /api/profile/me/testimonials/{testimonialId}",
        "description": "Delete a testimonial.",
        "tags": [
          "profile"
        ],
        "parameters": [
          {
            "name": "testimonialId",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Testimonial deleted successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "testimonial": {
                      "type": "object",
                      "properties": {
                        "id": {
                          "type": "string"
                        },
                        "clientName": {
                          "type": "string"
                        },
                        "testimonialText": {
                          "type": "string"
                        },
                        "rating": {
                          "type": "number",
                          "example": 0,
                          "nullable": true
                        }
                      },
                      "required": [
                        "id",
                        "clientName",
                        "testimonialText",
                        "rating"
                      ]
                    }
                  },
                  "required": [
                    "testimonial"
                  ]
                }
              }
            }
          },
          "404": {
            "description": "Testimonial not found."
          }
        }
      }
    },
    "/api/profile/me/social-links/{linkId}": {
      "put": {
        "summary": "PUT /api/profile/me/social-links/{linkId}",
        "description": "Update a social link.",
        "tags": [
          "profile"
        ],
        "parameters": [
          {
            "name": "linkId",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "platform": {
                    "type": "string",
                    "minLength": 1
                  },
                  "username": {
                    "type": "string",
                    "minLength": 1
                  },
                  "profileUrl": {
                    "type": "string",
                    "format": "url"
                  }
                },
                "required": [
                  "platform",
                  "username",
                  "profileUrl"
                ]
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "Link updated successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "updatedLink": {
                      "type": "object",
                      "properties": {
                        "id": {
                          "type": "string"
                        },
                        "platform": {
                          "type": "string"
                        },
                        "username": {
                          "type": "string"
                        },
                        "profileUrl": {
                          "type": "string"
                        }
                      },
                      "required": [
                        "id",
                        "platform",
                        "username",
                        "profileUrl"
                      ]
                    }
                  },
                  "required": [
                    "updatedLink"
                  ]
                }
              }
            }
          },
          "404": {
            "description": "Link not found."
          }
        }
      },
      "delete": {
        "summary": "DELETE /api/profile/me/social-links/{linkId}",
        "description": "Delete a social link.",
        "tags": [
          "profile"
        ],
        "parameters": [
          {
            "name": "linkId",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Link deleted successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "deletedId": {
                      "type": "string"
                    }
                  },
                  "required": [
                    "deletedId"
                  ]
                }
              }
            }
          },
          "404": {
            "description": "Link not found."
          }
        }
      }
    },
    "/api/profile/me/services/{serviceId}": {
      "put": {
        "summary": "PUT /api/profile/me/services/{serviceId}",
        "description": "Update a service.",
        "tags": [
          "profile"
        ],
        "parameters": [
          {
            "name": "serviceId",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "title": {
                    "type": "string",
                    "minLength": 1
                  },
                  "description": {
                    "type": "string",
                    "minLength": 1
                  },
                  "price": {
                    "type": "number",
                    "example": 0,
                    "nullable": true
                  },
                  "currency": {
                    "type": "string",
                    "minLength": 1,
                    "nullable": true
                  },
                  "duration": {
                    "type": "number",
                    "example": 0,
                    "nullable": true
                  }
                },
                "required": [
                  "title",
                  "description"
                ]
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "Service updated successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "service": {
                      "type": "object",
                      "properties": {
                        "id": {
                          "type": "string"
                        },
                        "title": {
                          "type": "string"
                        },
                        "description": {
                          "type": "string"
                        },
                        "price": {
                          "type": "string",
                          "nullable": true
                        }
                      },
                      "required": [
                        "id",
                        "title",
                        "description",
                        "price"
                      ]
                    }
                  },
                  "required": [
                    "service"
                  ]
                }
              }
            }
          },
          "404": {
            "description": "Service not found."
          }
        }
      },
      "delete": {
        "summary": "DELETE /api/profile/me/services/{serviceId}",
        "description": "Delete a service.",
        "tags": [
          "profile"
        ],
        "parameters": [
          {
            "name": "serviceId",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Service deleted successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "service": {
                      "type": "object",
                      "properties": {
                        "id": {
                          "type": "string"
                        },
                        "title": {
                          "type": "string"
                        },
                        "description": {
                          "type": "string"
                        },
                        "price": {
                          "type": "string",
                          "nullable": true
                        }
                      },
                      "required": [
                        "id",
                        "title",
                        "description",
                        "price"
                      ]
                    }
                  },
                  "required": [
                    "service"
                  ]
                }
              }
            }
          },
          "404": {
            "description": "Service not found."
          }
        }
      }
    },
    "/api/profile/me/packages/{packageId}": {
      "put": {
        "summary": "PUT /api/profile/me/packages/{packageId}",
        "description": "Update a package.",
        "tags": [
          "profile"
        ],
        "parameters": [
          {
            "name": "packageId",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "name": {
                    "type": "string",
                    "minLength": 2
                  },
                  "description": {
                    "type": "string",
                    "nullable": true
                  },
                  "price": {
                    "type": "number",
                    "example": 0
                  },
                  "numberOfSessions": {
                    "type": "number",
                    "example": 0
                  }
                },
                "required": [
                  "name",
                  "price",
                  "numberOfSessions"
                ]
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "Package updated successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "package": {
                      "type": "object",
                      "properties": {
                        "id": {
                          "type": "string"
                        },
                        "name": {
                          "type": "string"
                        },
                        "price": {
                          "type": "string"
                        },
                        "numberOfSessions": {
                          "type": "number",
                          "example": 0
                        },
                        "isActive": {
                          "type": "boolean"
                        }
                      },
                      "required": [
                        "id",
                        "name",
                        "price",
                        "numberOfSessions",
                        "isActive"
                      ]
                    }
                  },
                  "required": [
                    "package"
                  ]
                }
              }
            }
          },
          "404": {
            "description": "Package not found."
          }
        }
      },
      "delete": {
        "summary": "DELETE /api/profile/me/packages/{packageId}",
        "description": "Archive a package.",
        "tags": [
          "profile"
        ],
        "parameters": [
          {
            "name": "packageId",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Package archived successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "package": {
                      "type": "object",
                      "properties": {
                        "id": {
                          "type": "string"
                        },
                        "name": {
                          "type": "string"
                        },
                        "price": {
                          "type": "string"
                        },
                        "numberOfSessions": {
                          "type": "number",
                          "example": 0
                        },
                        "isActive": {
                          "type": "boolean"
                        }
                      },
                      "required": [
                        "id",
                        "name",
                        "price",
                        "numberOfSessions",
                        "isActive"
                      ]
                    }
                  },
                  "required": [
                    "package"
                  ]
                }
              }
            }
          },
          "404": {
            "description": "Package not found."
          }
        }
      }
    },
    "/api/profile/me/external-links/{linkId}": {
      "put": {
        "summary": "PUT /api/profile/me/external-links/{linkId}",
        "description": "Update an external link.",
        "tags": [
          "profile"
        ],
        "parameters": [
          {
            "name": "linkId",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "label": {
                    "type": "string",
                    "minLength": 1
                  },
                  "linkUrl": {
                    "type": "string",
                    "format": "url"
                  }
                },
                "required": [
                  "label",
                  "linkUrl"
                ]
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "Link updated successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "updatedLink": {
                      "type": "object",
                      "properties": {
                        "id": {
                          "type": "string"
                        },
                        "label": {
                          "type": "string"
                        },
                        "linkUrl": {
                          "type": "string"
                        }
                      },
                      "required": [
                        "id",
                        "label",
                        "linkUrl"
                      ]
                    }
                  },
                  "required": [
                    "updatedLink"
                  ]
                }
              }
            }
          }
        }
      },
      "delete": {
        "summary": "DELETE /api/profile/me/external-links/{linkId}",
        "description": "Delete an external link.",
        "tags": [
          "profile"
        ],
        "parameters": [
          {
            "name": "linkId",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Link deleted successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "deletedId": {
                      "type": "string"
                    }
                  },
                  "required": [
                    "deletedId"
                  ]
                }
              }
            }
          }
        }
      }
    },
    "/api/profile/me/exercises/{exerciseId}": {
      "put": {
        "summary": "PUT /api/profile/me/exercises/{exerciseId}",
        "description": "Update a custom exercise.",
        "tags": [
          "profile"
        ],
        "parameters": [
          {
            "name": "exerciseId",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "name": {
                    "type": "string",
                    "minLength": 2
                  },
                  "muscleGroup": {
                    "type": "string"
                  },
                  "equipment": {
                    "type": "string"
                  },
                  "videoUrl": {}
                },
                "required": [
                  "name",
                  "videoUrl"
                ]
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "Exercise updated successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "updatedExercise": {
                      "type": "object",
                      "properties": {
                        "id": {
                          "type": "string"
                        },
                        "name": {
                          "type": "string"
                        }
                      },
                      "required": [
                        "id",
                        "name"
                      ]
                    }
                  },
                  "required": [
                    "updatedExercise"
                  ]
                }
              }
            }
          },
          "403": {
            "description": "Forbidden."
          }
        }
      },
      "delete": {
        "summary": "DELETE /api/profile/me/exercises/{exerciseId}",
        "description": "Delete a custom exercise.",
        "tags": [
          "profile"
        ],
        "parameters": [
          {
            "name": "exerciseId",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Exercise deleted successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "deletedId": {
                      "type": "string"
                    }
                  },
                  "required": [
                    "deletedId"
                  ]
                }
              }
            }
          }
        }
      }
    },
    "/api/profile/me/benefits/{benefitId}": {
      "put": {
        "summary": "PUT /api/profile/me/benefits/{benefitId}",
        "description": "Update a specific benefit.",
        "tags": [
          "profile"
        ],
        "parameters": [
          {
            "name": "benefitId",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "title": {
                    "type": "string",
                    "minLength": 1
                  },
                  "description": {
                    "type": "string",
                    "nullable": true
                  },
                  "iconName": {
                    "type": "string",
                    "nullable": true
                  },
                  "iconStyle": {
                    "type": "string",
                    "nullable": true
                  }
                },
                "required": [
                  "title"
                ]
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "Benefit updated successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "updatedBenefit": {
                      "type": "object",
                      "properties": {
                        "id": {
                          "type": "string"
                        },
                        "title": {
                          "type": "string"
                        },
                        "description": {
                          "type": "string",
                          "nullable": true
                        }
                      },
                      "required": [
                        "id",
                        "title",
                        "description"
                      ]
                    }
                  },
                  "required": [
                    "updatedBenefit"
                  ]
                }
              }
            }
          },
          "404": {
            "description": "Benefit or profile not found."
          }
        }
      },
      "delete": {
        "summary": "DELETE /api/profile/me/benefits/{benefitId}",
        "description": "Delete a specific benefit.",
        "tags": [
          "profile"
        ],
        "parameters": [
          {
            "name": "benefitId",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Benefit deleted successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "deletedId": {
                      "type": "string"
                    }
                  },
                  "required": [
                    "deletedId"
                  ]
                }
              }
            }
          },
          "404": {
            "description": "Benefit or profile not found."
          }
        }
      }
    },
    "/api/profile/me/benefits/order": {
      "put": {
        "summary": "PUT /api/profile/me/benefits/order",
        "description": "Update the order of benefits.",
        "tags": [
          "profile"
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "ids": {
                    "type": "array",
                    "items": {
                      "type": "string"
                    }
                  }
                },
                "required": [
                  "ids"
                ]
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "Order updated successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "message": {
                      "type": "string"
                    }
                  },
                  "required": [
                    "message"
                  ]
                }
              }
            }
          },
          "400": {
            "description": "Invalid payload."
          }
        }
      }
    },
    "/api/clients/{id}/sessions/{sessionId}": {
      "put": {
        "summary": "PUT /api/clients/{id}/sessions/{sessionId}",
        "description": "Update a workout session log.",
        "tags": [
          "clients"
        ],
        "parameters": [
          {
            "name": "id",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          },
          {
            "name": "sessionId",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "sessionDate": {
                    "type": "string"
                  },
                  "durationMinutes": {
                    "type": "number",
                    "example": 0
                  },
                  "activitySummary": {
                    "type": "string"
                  },
                  "sessionNotes": {
                    "type": "string",
                    "nullable": true
                  }
                },
                "required": [
                  "sessionDate",
                  "durationMinutes",
                  "activitySummary"
                ]
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "Successfully updated session.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "updatedSession": {
                      "type": "object",
                      "properties": {
                        "id": {
                          "type": "string"
                        },
                        "startTime": {
                          "type": "string"
                        },
                        "endTime": {
                          "type": "string",
                          "nullable": true
                        },
                        "notes": {
                          "type": "string",
                          "nullable": true
                        }
                      },
                      "required": [
                        "id",
                        "startTime",
                        "endTime",
                        "notes"
                      ]
                    }
                  },
                  "required": [
                    "updatedSession"
                  ]
                }
              }
            }
          },
          "404": {
            "description": "Session not found or unauthorized."
          },
          "422": {
            "description": "Validation failed."
          }
        }
      },
      "delete": {
        "summary": "DELETE /api/clients/{id}/sessions/{sessionId}",
        "description": "Delete a workout session log.",
        "tags": [
          "clients"
        ],
        "parameters": [
          {
            "name": "id",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          },
          {
            "name": "sessionId",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Successfully deleted session.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "deletedId": {
                      "type": "string"
                    }
                  },
                  "required": [
                    "deletedId"
                  ]
                }
              }
            }
          },
          "404": {
            "description": "Session not found or unauthorized."
          }
        }
      }
    },
    "/api/clients/{id}/session/active": {
      "get": {
        "summary": "GET /api/clients/{id}/session/active",
        "description": "Retrieve the currently active workout session for a client.",
        "tags": [
          "clients"
        ],
        "parameters": [
          {
            "name": "id",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Successfully retrieved active session.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "session": {
                      "type": "object",
                      "properties": {
                        "id": {
                          "type": "string"
                        },
                        "status": {
                          "type": "string",
                          "enum": [
                            "IN_PROGRESS"
                          ]
                        },
                        "client": {
                          "type": "object",
                          "properties": {
                            "id": {
                              "type": "string"
                            },
                            "name": {
                              "type": "string"
                            }
                          },
                          "required": [
                            "id",
                            "name"
                          ]
                        }
                      },
                      "required": [
                        "id",
                        "status"
                      ]
                    }
                  },
                  "required": [
                    "session"
                  ]
                }
              }
            }
          },
          "400": {
            "description": "Client ID is required."
          },
          "404": {
            "description": "Client not found or no active session."
          }
        }
      }
    },
    "/api/clients/{id}/photos/{photoId}": {
      "delete": {
        "summary": "DELETE /api/clients/{id}/photos/{photoId}",
        "description": "Delete a progress photo.",
        "tags": [
          "clients"
        ],
        "parameters": [
          {
            "name": "id",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          },
          {
            "name": "photoId",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Successfully deleted photo.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "photo": {
                      "type": "object",
                      "properties": {
                        "id": {
                          "type": "string"
                        }
                      },
                      "required": [
                        "id"
                      ]
                    }
                  },
                  "required": [
                    "photo"
                  ]
                }
              }
            }
          },
          "400": {
            "description": "Photo ID is required."
          }
        }
      }
    },
    "/api/clients/{id}/measurements/{measurementId}": {
      "put": {
        "summary": "PUT /api/clients/{id}/measurements/{measurementId}",
        "description": "Update a measurement record.",
        "tags": [
          "clients"
        ],
        "parameters": [
          {
            "name": "id",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          },
          {
            "name": "measurementId",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "measurementDate": {
                    "type": "string"
                  },
                  "weightKg": {
                    "type": "number",
                    "example": 0,
                    "nullable": true
                  },
                  "bodyFatPercentage": {
                    "type": "number",
                    "example": 0,
                    "nullable": true
                  },
                  "notes": {
                    "type": "string",
                    "nullable": true
                  },
                  "customMetrics": {
                    "nullable": true
                  }
                },
                "required": [
                  "measurementDate"
                ]
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "Successfully updated measurement.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "measurement": {
                      "type": "object",
                      "properties": {
                        "id": {
                          "type": "string"
                        }
                      },
                      "required": [
                        "id"
                      ]
                    }
                  },
                  "required": [
                    "measurement"
                  ]
                }
              }
            }
          },
          "400": {
            "description": "Invalid input."
          },
          "422": {
            "description": "Validation failed."
          }
        }
      },
      "delete": {
        "summary": "DELETE /api/clients/{id}/measurements/{measurementId}",
        "description": "Delete a measurement record.",
        "tags": [
          "clients"
        ],
        "parameters": [
          {
            "name": "id",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          },
          {
            "name": "measurementId",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Successfully deleted measurement.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "measurement": {
                      "type": "object",
                      "properties": {
                        "id": {
                          "type": "string"
                        }
                      },
                      "required": [
                        "id"
                      ]
                    }
                  },
                  "required": [
                    "measurement"
                  ]
                }
              }
            }
          },
          "400": {
            "description": "Invalid IDs."
          }
        }
      }
    },
    "/api/clients/{id}/assessments/{resultId}": {
      "put": {
        "summary": "PUT /api/clients/{id}/assessments/{resultId}",
        "description": "Update an assessment result.",
        "tags": [
          "clients"
        ],
        "parameters": [
          {
            "name": "id",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          },
          {
            "name": "resultId",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "assessmentId": {
                    "type": "string"
                  },
                  "date": {
                    "type": "string"
                  },
                  "value": {
                    "type": "number",
                    "example": 0
                  },
                  "notes": {
                    "type": "string"
                  }
                },
                "required": [
                  "assessmentId",
                  "date",
                  "value"
                ]
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "Successfully updated assessment result.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "assessmentResult": {
                      "type": "object",
                      "properties": {
                        "id": {
                          "type": "string"
                        }
                      },
                      "required": [
                        "id"
                      ]
                    }
                  },
                  "required": [
                    "assessmentResult"
                  ]
                }
              }
            }
          },
          "400": {
            "description": "Invalid input."
          }
        }
      },
      "delete": {
        "summary": "DELETE /api/clients/{id}/assessments/{resultId}",
        "description": "Delete an assessment result.",
        "tags": [
          "clients"
        ],
        "parameters": [
          {
            "name": "id",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          },
          {
            "name": "resultId",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Successfully deleted assessment result.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "assessmentResult": {
                      "type": "object",
                      "properties": {
                        "id": {
                          "type": "string"
                        }
                      },
                      "required": [
                        "id"
                      ]
                    }
                  },
                  "required": [
                    "assessmentResult"
                  ]
                }
              }
            }
          },
          "400": {
            "description": "Result ID is required."
          }
        }
      }
    },
    "/api/trainer/programs/templates/{templateId}/rest": {
      "post": {
        "summary": "POST /api/trainer/programs/templates/{templateId}/rest",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "trainer"
        ],
        "parameters": [
          {
            "name": "templateId",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "durationSeconds": {
                    "type": "number",
                    "example": 0
                  }
                },
                "required": [
                  "durationSeconds"
                ]
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "200 response."
          },
          "201": {
            "description": "201 response."
          },
          "422": {
            "description": "422 response."
          }
        }
      }
    },
    "/api/trainer/programs/templates/{templateId}/exercises": {
      "post": {
        "summary": "POST /api/trainer/programs/templates/{templateId}/exercises",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "trainer"
        ],
        "parameters": [
          {
            "name": "templateId",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "exerciseId": {
                    "type": "string"
                  },
                  "targetReps": {
                    "type": "string",
                    "nullable": true
                  },
                  "durationSeconds": {
                    "type": "number",
                    "example": 0,
                    "nullable": true
                  },
                  "notes": {
                    "type": "string",
                    "nullable": true
                  }
                },
                "required": [
                  "exerciseId"
                ]
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "200 response."
          },
          "201": {
            "description": "201 response."
          },
          "422": {
            "description": "422 response."
          }
        }
      }
    },
    "/api/trainer/programs/templates/{templateId}/copy": {
      "post": {
        "summary": "POST /api/trainer/programs/templates/{templateId}/copy",
        "description": "Copy a system template to the trainer's library.",
        "tags": [
          "trainer"
        ],
        "parameters": [
          {
            "name": "templateId",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "201": {
            "description": "Template copied successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "newTemplate": {
                      "type": "object",
                      "properties": {
                        "id": {
                          "type": "string"
                        },
                        "name": {
                          "type": "string"
                        },
                        "programId": {
                          "type": "string"
                        }
                      },
                      "required": [
                        "id",
                        "name",
                        "programId"
                      ]
                    },
                    "newProgram": {
                      "type": "object",
                      "properties": {
                        "id": {
                          "type": "string"
                        },
                        "name": {
                          "type": "string"
                        }
                      },
                      "required": [
                        "id",
                        "name"
                      ],
                      "nullable": true
                    }
                  },
                  "required": [
                    "newTemplate",
                    "newProgram"
                  ]
                }
              }
            }
          }
        }
      }
    },
    "/api/trainer/calendar/sessions/{sessionId}/remind": {
      "post": {
        "summary": "POST /api/trainer/calendar/sessions/{sessionId}/remind",
        "description": "Send a reminder to the client for a planned session.",
        "tags": [
          "trainer"
        ],
        "parameters": [
          {
            "name": "sessionId",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Reminder sent successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "message": {
                      "type": "string"
                    }
                  },
                  "required": [
                    "message"
                  ]
                }
              }
            }
          },
          "400": {
            "description": "Client does not have a user account linked."
          },
          "404": {
            "description": "Session not found."
          }
        }
      }
    },
    "/api/trainer/programs/templates/{templateId}/exercises/{exerciseStepId}": {
      "delete": {
        "summary": "DELETE /api/trainer/programs/templates/{templateId}/exercises/{exerciseStepId}",
        "description": "Remove an exercise step from a template.",
        "tags": [
          "trainer"
        ],
        "parameters": [
          {
            "name": "templateId",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          },
          {
            "name": "exerciseStepId",
            "in": "path",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "Exercise step deleted successfully.",
            "content": {
              "application/json": {
                "schema": {
                  "type": "object",
                  "properties": {
                    "message": {
                      "type": "string"
                    }
                  },
                  "required": [
                    "message"
                  ]
                }
              }
            }
          }
        }
      }
    }
  },
  "tags": [
    {
      "name": "trainers",
      "description": "Trainers endpoints."
    },
    {
      "name": "openapi",
      "description": "Openapi endpoints."
    },
    {
      "name": "notifications",
      "description": "Notifications endpoints."
    },
    {
      "name": "exercises",
      "description": "Exercises endpoints."
    },
    {
      "name": "dashboard",
      "description": "Dashboard endpoints."
    },
    {
      "name": "contact",
      "description": "Contact endpoints."
    },
    {
      "name": "clients",
      "description": "Clients endpoints."
    },
    {
      "name": "bookings",
      "description": "Bookings endpoints."
    },
    {
      "name": "workout-sessions",
      "description": "Workout-sessions endpoints."
    },
    {
      "name": "webhooks",
      "description": "Webhooks endpoints."
    },
    {
      "name": "workout",
      "description": "Workout endpoints."
    },
    {
      "name": "user",
      "description": "User endpoints."
    },
    {
      "name": "trainer",
      "description": "Trainer endpoints."
    },
    {
      "name": "sync",
      "description": "Sync endpoints."
    },
    {
      "name": "profile",
      "description": "Profile endpoints."
    },
    {
      "name": "cron",
      "description": "Cron endpoints."
    },
    {
      "name": "client",
      "description": "Client endpoints."
    },
    {
      "name": "checkout",
      "description": "Checkout endpoints."
    },
    {
      "name": "billing",
      "description": "Billing endpoints."
    },
    {
      "name": "auth",
      "description": "Auth endpoints."
    },
    {
      "name": "public",
      "description": "Public endpoints."
    }
  ],
  "x-generated-at": "2025-12-18T11:54:38.774Z"
}