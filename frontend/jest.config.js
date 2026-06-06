module.exports = {
    testEnvironment: 'node',
    transform: {
        '^.+\\.ts$': ['ts-jest', {
            tsconfig: {
                esModuleInterop: true
            }
        }]
    },
    moduleFileExtensions: ['ts', 'js', 'json'],
    testPathIgnorePatterns: ['<rootDir>/node_modules/'],
    transformIgnorePatterns: ['<rootDir>/node_modules/']
};