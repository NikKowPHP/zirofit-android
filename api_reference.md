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
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "trainers"
        ],
        "responses": {
          "200": {
            "description": "Successful response."
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
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "notifications"
        ],
        "responses": {
          "200": {
            "description": "Successful response."
          }
        }
      }
    },
    "/api/exercises": {
      "get": {
        "summary": "GET /api/exercises",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "exercises"
        ],
        "responses": {
          "200": {
            "description": "Successful response."
          }
        }
      }
    },
    "/api/dashboard": {
      "get": {
        "summary": "GET /api/dashboard",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "dashboard"
        ],
        "responses": {
          "200": {
            "description": "Successful response."
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
                  "email": {

                  },
                  "phone": {

                  },
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
        "description": "Create a new booking request.",
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
                  "clientName": {
                    "type": "string"
                  },
                  "clientEmail": {
                    "type": "string",
                    "format": "email"
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
          "400": {
            "description": "Invalid input or missing fields for guest booking."
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
            "description": "200 response."
          },
          "400": {
            "description": "400 response."
          }
        }
      }
    },
    "/api/workout-sessions/live": {
      "get": {
        "summary": "GET /api/workout-sessions/live",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "workout-sessions"
        ],
        "responses": {
          "200": {
            "description": "Successful response."
          }
        }
      },
      "post": {
        "summary": "POST /api/workout-sessions/live",
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
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "workout-sessions"
        ],
        "responses": {
          "200": {
            "description": "Successful response."
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
            "description": "Successful response."
          }
        }
      },
      "put": {
        "summary": "PUT /api/workout-sessions/{id}",
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
            "description": "Successful response."
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
    "/api/user/tier": {
      "get": {
        "summary": "GET /api/user/tier",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "user"
        ],
        "responses": {
          "200": {
            "description": "Successful response."
          }
        }
      }
    },
    "/api/user/delete": {
      "delete": {
        "summary": "DELETE /api/user/delete",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "user"
        ],
        "responses": {
          "200": {
            "description": "Successful response."
          }
        }
      }
    },
    "/api/trainers/specialties": {
      "get": {
        "summary": "GET /api/trainers/specialties",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "trainers"
        ],
        "responses": {
          "200": {
            "description": "Successful response."
          }
        }
      }
    },
    "/api/trainers/{username}": {
      "get": {
        "summary": "GET /api/trainers/{username}",
        "description": "Auto-generated from Next.js route handler.",
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
            "description": "Successful response."
          }
        }
      }
    },
    "/api/trainer/settings": {
      "get": {
        "summary": "GET /api/trainer/settings",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "trainer"
        ],
        "responses": {
          "200": {
            "description": "Successful response."
          }
        }
      },
      "patch": {
        "summary": "PATCH /api/trainer/settings",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "trainer"
        ],
        "responses": {
          "200": {
            "description": "Successful response."
          }
        }
      }
    },
    "/api/trainer/session-creation-data": {
      "get": {
        "summary": "GET /api/trainer/session-creation-data",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "trainer"
        ],
        "responses": {
          "200": {
            "description": "Successful response."
          }
        }
      }
    },
    "/api/trainer/programs": {
      "get": {
        "summary": "GET /api/trainer/programs",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "trainer"
        ],
        "responses": {
          "200": {
            "description": "Successful response."
          }
        }
      },
      "post": {
        "summary": "POST /api/trainer/programs",
        "description": "Auto-generated from Next.js route handler.",
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
    "/api/trainer/profile": {
      "get": {
        "summary": "GET /api/trainer/profile",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "trainer"
        ],
        "responses": {
          "200": {
            "description": "Successful response."
          }
        }
      }
    },
    "/api/trainer/clients": {
      "get": {
        "summary": "GET /api/trainer/clients",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "trainer"
        ],
        "responses": {
          "200": {
            "description": "Successful response."
          }
        }
      },
      "post": {
        "summary": "POST /api/trainer/clients",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "trainer"
        ],
        "responses": {
          "200": {
            "description": "Successful response."
          }
        }
      }
    },
    "/api/trainer/calendar": {
      "get": {
        "summary": "GET /api/trainer/calendar",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "trainer"
        ],
        "responses": {
          "200": {
            "description": "Successful response."
          }
        }
      },
      "post": {
        "summary": "POST /api/trainer/calendar",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "trainer"
        ],
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
          "409": {
            "description": "409 response."
          },
          "422": {
            "description": "422 response."
          }
        }
      }
    },
    "/api/trainer/assessments": {
      "get": {
        "summary": "GET /api/trainer/assessments",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "trainer"
        ],
        "responses": {
          "200": {
            "description": "Successful response."
          }
        }
      }
    },
    "/api/sync/pull": {
      "get": {
        "summary": "GET /api/sync/pull",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "sync"
        ],
        "responses": {
          "200": {
            "description": "Successful response."
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
    "/api/profile/me": {
      "get": {
        "summary": "GET /api/profile/me",
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
    "/api/notifications/{id}": {
      "put": {
        "summary": "PUT /api/notifications/{id}",
        "description": "Auto-generated from Next.js route handler.",
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
            "description": "Successful response."
          }
        }
      }
    },
    "/api/exercises/find-media": {
      "get": {
        "summary": "GET /api/exercises/find-media",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "exercises"
        ],
        "responses": {
          "200": {
            "description": "Successful response."
          }
        }
      }
    },
    "/api/dashboard/insights": {
      "get": {
        "summary": "GET /api/dashboard/insights",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "dashboard"
        ],
        "responses": {
          "200": {
            "description": "Successful response."
          }
        }
      }
    },
    "/api/dashboard/summary": {
      "get": {
        "summary": "GET /api/dashboard/summary",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "dashboard"
        ],
        "responses": {
          "200": {
            "description": "Successful response."
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
    "/api/client/dashboard": {
      "get": {
        "summary": "GET /api/client/dashboard",
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
    "/api/client/check-in": {
      "post": {
        "summary": "POST /api/client/check-in",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "client"
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
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "billing"
        ],
        "responses": {
          "200": {
            "description": "Successful response."
          }
        }
      },
      "post": {
        "summary": "POST /api/billing/subscription",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "billing"
        ],
        "responses": {
          "200": {
            "description": "200 response."
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
      "patch": {
        "summary": "PATCH /api/billing/subscription",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "billing"
        ],
        "responses": {
          "200": {
            "description": "Successful response."
          }
        }
      }
    },
    "/api/billing/subscribe-new": {
      "post": {
        "summary": "POST /api/billing/subscribe-new",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "billing"
        ],
        "responses": {
          "200": {
            "description": "200 response."
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
    "/api/billing/portal": {
      "post": {
        "summary": "POST /api/billing/portal",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "billing"
        ],
        "responses": {
          "200": {
            "description": "200 response."
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
    "/api/auth/sync-user": {
      "post": {
        "summary": "POST /api/auth/sync-user",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "auth"
        ],
        "responses": {
          "200": {
            "description": "200 response."
          }
        }
      }
    },
    "/api/auth/signout": {
      "post": {
        "summary": "POST /api/auth/signout",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "auth"
        ],
        "responses": {
          "200": {
            "description": "200 response."
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
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "auth"
        ],
        "responses": {
          "200": {
            "description": "Successful response."
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
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "auth"
        ],
        "responses": {
          "200": {
            "description": "200 response."
          }
        }
      }
    },
    "/api/workout-sessions/{id}/summary": {
      "get": {
        "summary": "GET /api/workout-sessions/{id}/summary",
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
            "description": "Successful response."
          }
        }
      }
    },
    "/api/workout-sessions/{id}/save-as-template": {
      "post": {
        "summary": "POST /api/workout-sessions/{id}/save-as-template",
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
          "422": {
            "description": "422 response."
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
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "workout"
        ],
        "responses": {
          "200": {
            "description": "Successful response."
          }
        }
      }
    },
    "/api/trainers/{username}/transformation-photos": {
      "get": {
        "summary": "GET /api/trainers/{username}/transformation-photos",
        "description": "Auto-generated from Next.js route handler.",
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
            "description": "Successful response."
          }
        }
      }
    },
    "/api/trainers/{username}/testimonials": {
      "get": {
        "summary": "GET /api/trainers/{username}/testimonials",
        "description": "Auto-generated from Next.js route handler.",
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
            "description": "Successful response."
          }
        }
      }
    },
    "/api/trainers/{username}/schedule": {
      "get": {
        "summary": "GET /api/trainers/{username}/schedule",
        "description": "Auto-generated from Next.js route handler.",
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
            "description": "Successful response."
          }
        }
      }
    },
    "/api/trainers/{username}/packages": {
      "get": {
        "summary": "GET /api/trainers/{username}/packages",
        "description": "Auto-generated from Next.js route handler.",
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
            "description": "Successful response."
          }
        }
      }
    },
    "/api/trainer/programs/templates": {
      "post": {
        "summary": "POST /api/trainer/programs/templates",
        "description": "Auto-generated from Next.js route handler.",
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
      },
      "get": {
        "summary": "GET /api/trainer/programs/templates",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "trainer"
        ],
        "responses": {
          "200": {
            "description": "Successful response."
          }
        }
      }
    },
    "/api/trainer/clients/{id}": {
      "get": {
        "summary": "GET /api/trainer/clients/{id}",
        "description": "Auto-generated from Next.js route handler.",
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
            "description": "Successful response."
          }
        }
      },
      "put": {
        "summary": "PUT /api/trainer/clients/{id}",
        "description": "Auto-generated from Next.js route handler.",
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
            "description": "Successful response."
          }
        }
      },
      "delete": {
        "summary": "DELETE /api/trainer/clients/{id}",
        "description": "Auto-generated from Next.js route handler.",
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
            "description": "Successful response."
          }
        }
      }
    },
    "/api/trainer/check-ins/pending": {
      "get": {
        "summary": "GET /api/trainer/check-ins/pending",
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "trainer"
        ],
        "responses": {
          "200": {
            "description": "Successful response."
          }
        }
      }
    },
    "/api/trainer/check-ins/{id}": {
      "get": {
        "summary": "GET /api/trainer/check-ins/{id}",
        "description": "Auto-generated from Next.js route handler.",
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
            "description": "Successful response."
          }
        }
      }
    },
    "/api/trainer/calendar/{sessionId}": {
      "put": {
        "summary": "PUT /api/trainer/calendar/{sessionId}",
        "description": "Auto-generated from Next.js route handler.",
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
            "description": "Successful response."
          }
        }
      },
      "delete": {
        "summary": "DELETE /api/trainer/calendar/{sessionId}",
        "description": "Auto-generated from Next.js route handler.",
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
            "description": "Successful response."
          }
        }
      }
    },
    "/api/public/workout-summary/{sessionId}": {
      "get": {
        "summary": "GET /api/public/workout-summary/{sessionId}",
        "description": "Auto-generated from Next.js route handler.",
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
            "description": "Successful response."
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
      "put": {
        "summary": "PUT /api/profile/me/text-content",
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
                  "videoUrl": {

                  }
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
      "put": {
        "summary": "PUT /api/profile/me/core-info",
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
        "summary": "POST /api/profile/me/benefits",
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
    "/api/profile/me/availability": {
      "get": {
        "summary": "GET /api/profile/me/availability",
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
      "put": {
        "summary": "PUT /api/profile/me/availability",
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
    "/api/profile/me/assessments": {
      "get": {
        "summary": "GET /api/profile/me/assessments",
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
        "summary": "POST /api/profile/me/assessments",
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
          "200": {
            "description": "200 response."
          },
          "201": {
            "description": "201 response."
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
                    "analysis": {

                    }
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
                  "sets": {

                  }
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
                        "sets": {

                        }
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
                      "items": {

                      }
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
        "description": "Auto-generated from Next.js route handler.",
        "tags": [
          "client"
        ],
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
          "404": {
            "description": "404 response."
          }
        }
      },
      "delete": {
        "summary": "DELETE /api/client/trainer/link",
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
        "description": "Auto-generated from Next.js route handler.",
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
            "description": "Successful response."
          }
        }
      }
    },
    "/api/profile/me/transformation-photos/{photoId}": {
      "delete": {
        "summary": "DELETE /api/profile/me/transformation-photos/{photoId}",
        "description": "Auto-generated from Next.js route handler.",
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
            "description": "Successful response."
          }
        }
      }
    },
    "/api/profile/me/testimonials/{testimonialId}": {
      "put": {
        "summary": "PUT /api/profile/me/testimonials/{testimonialId}",
        "description": "Auto-generated from Next.js route handler.",
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
            "description": "Successful response."
          }
        }
      },
      "delete": {
        "summary": "DELETE /api/profile/me/testimonials/{testimonialId}",
        "description": "Auto-generated from Next.js route handler.",
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
            "description": "Successful response."
          }
        }
      }
    },
    "/api/profile/me/social-links/{linkId}": {
      "put": {
        "summary": "PUT /api/profile/me/social-links/{linkId}",
        "description": "Auto-generated from Next.js route handler.",
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
            "description": "Successful response."
          }
        }
      },
      "delete": {
        "summary": "DELETE /api/profile/me/social-links/{linkId}",
        "description": "Auto-generated from Next.js route handler.",
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
            "description": "Successful response."
          }
        }
      }
    },
    "/api/profile/me/services/{serviceId}": {
      "put": {
        "summary": "PUT /api/profile/me/services/{serviceId}",
        "description": "Auto-generated from Next.js route handler.",
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
            "description": "Successful response."
          }
        }
      },
      "delete": {
        "summary": "DELETE /api/profile/me/services/{serviceId}",
        "description": "Auto-generated from Next.js route handler.",
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
            "description": "Successful response."
          }
        }
      }
    },
    "/api/profile/me/packages/{packageId}": {
      "put": {
        "summary": "PUT /api/profile/me/packages/{packageId}",
        "description": "Auto-generated from Next.js route handler.",
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
            "description": "Successful response."
          }
        }
      },
      "delete": {
        "summary": "DELETE /api/profile/me/packages/{packageId}",
        "description": "Auto-generated from Next.js route handler.",
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
            "description": "Successful response."
          }
        }
      }
    },
    "/api/profile/me/external-links/{linkId}": {
      "put": {
        "summary": "PUT /api/profile/me/external-links/{linkId}",
        "description": "Auto-generated from Next.js route handler.",
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
            "description": "Successful response."
          }
        }
      },
      "delete": {
        "summary": "DELETE /api/profile/me/external-links/{linkId}",
        "description": "Auto-generated from Next.js route handler.",
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
            "description": "Successful response."
          }
        }
      }
    },
    "/api/profile/me/exercises/{exerciseId}": {
      "put": {
        "summary": "PUT /api/profile/me/exercises/{exerciseId}",
        "description": "Auto-generated from Next.js route handler.",
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
            "description": "Successful response."
          }
        }
      },
      "delete": {
        "summary": "DELETE /api/profile/me/exercises/{exerciseId}",
        "description": "Auto-generated from Next.js route handler.",
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
            "description": "Successful response."
          }
        }
      }
    },
    "/api/profile/me/benefits/order": {
      "put": {
        "summary": "PUT /api/profile/me/benefits/order",
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
    "/api/profile/me/benefits/{benefitId}": {
      "put": {
        "summary": "PUT /api/profile/me/benefits/{benefitId}",
        "description": "Auto-generated from Next.js route handler.",
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
            "description": "Successful response."
          }
        }
      },
      "delete": {
        "summary": "DELETE /api/profile/me/benefits/{benefitId}",
        "description": "Auto-generated from Next.js route handler.",
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
            "description": "Successful response."
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
        "responses": {
          "200": {
            "description": "200 response."
          },
          "201": {
            "description": "201 response."
          }
        }
      }
    },
    "/api/trainer/calendar/sessions/{sessionId}/remind": {
      "post": {
        "summary": "POST /api/trainer/calendar/sessions/{sessionId}/remind",
        "description": "Auto-generated from Next.js route handler.",
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
            "description": "200 response."
          },
          "400": {
            "description": "400 response."
          },
          "404": {
            "description": "404 response."
          }
        }
      }
    },
    "/api/trainer/programs/templates/{templateId}/exercises/{exerciseStepId}": {
      "delete": {
        "summary": "DELETE /api/trainer/programs/templates/{templateId}/exercises/{exerciseStepId}",
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
            "description": "Successful response."
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
      "name": "workout",
      "description": "Workout endpoints."
    },
    {
      "name": "webhooks",
      "description": "Webhooks endpoints."
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
  "x-generated-at": "2025-12-16T08:27:15.609Z"
}