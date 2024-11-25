export default {
    moduleNameMapper: {
        '^@/(.*)$': '<rootDir>/src/$1',
        '\\.css$': 'identity-obj-proxy',
        '\\.(png|jpg|jpeg|svg|gif)$': '<rootDir>/__mocks__/fileMock.js',
    },
    setupFilesAfterEnv: ['<rootDir>/jest.setup.js'],
    testEnvironment: 'jsdom',
    transform: {
        '^.+\\.(js|jsx|ts|tsx)$': 'babel-jest',
        "^.+\\.tsx?$": "ts-jest",
    },
    moduleDirectories: ['node_modules', 'src'],
    transformIgnorePatterns: [
        "node_modules/(?!(axios)/)"
    ],
};


