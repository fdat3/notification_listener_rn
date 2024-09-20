const express = require('express');
const http = require('http');
const { Server: SocketIOServer } = require('socket.io');
const redis = require('redis');
const app = require('./../../app');
const query_redis_service = require('../../services/query_redis_service_cache.js');

/** 
  Back-end service

  Handle when kill the application, push notification booking or promotion notification.
  Use-case: 

    - Promotion Notification like Shopee
    - Booking Notification like Food Order App ( Tracking Procees when Order Success  -> Delivering -> Finish ), will push noti.

*/

let connections = [];

class SocketService {
    constructor() {
        this.httpServer = http.createServer(app);
        this.io = new SocketIOServer(this.httpServer, {
            cors: {
                origin: '*',
                methods: ["GET", "POST"]
            }
        });

        this.redisClient = redis.createClient();

        this.redisClient.subscribe('notification-booking-process');
        this.redisClient.subscribe('notification-promotion');
    }

    init() {
        this.httpServer.listen(8080, () =>
            console.log(`🚀 Socket server listening on port 8080`),
        );

        this.redisClient.on('message', async (channel, message) => {
            // Logic to handle notification and redis service
        });

        this.io.on("connection", (socket) => {
            // Handle socket connection and event
        });
    }
}

module.exports = new SocketService();
