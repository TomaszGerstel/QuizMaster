const fs = require('fs');
const path = require('path');

// Read .env file
const envPath = path.resolve(__dirname, '.env');
const envConfig = fs.readFileSync(envPath, 'utf8');

// Parse .env file
const envVariables = {};
envConfig.split('\n').forEach(line => {
    const [key, value] = line.split('=');
    if (key && value) {
        envVariables[key.trim()] = value.trim();
    }
});

const MONGO_USER = envVariables.MONGO_USER;
const MONGO_PASS = envVariables.MONGO_PASS;

db = db.getSiblingDB('quizmaster');
db.createUser(
    {
        user: MONGO_USER,
        pwd: MONGO_PASS,
        roles: [
            {role: "readWrite", db: "quizmaster"},
            {role: "dbAdmin", db: "quizmaster"}
        ]
    }
)