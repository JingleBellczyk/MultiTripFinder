# Stage 1: Build the React app
FROM node:18 AS build

# Set the working directory in the container
WORKDIR /app

# Copy package.json and package-lock.json to the container
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the rest of the application code to the container
COPY . .

# Build the React app
RUN npm run build

# Stage 2: Serve the React app with a lightweight server
FROM nginx:alpine

# Copy the build output from the previous stage to the nginx html directory
COPY --from=build /app/build /usr/share/nginx/html

COPY nginx.conf /etc/nginx/nginx.conf

# Expose port 80 to the outside world
EXPOSE 80

# Start nginx when the container launches
CMD ["nginx", "-g", "daemon off;"]
