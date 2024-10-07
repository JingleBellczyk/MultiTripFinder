# Dla twórców:

1.1 Lanuching app
- you need to have installed:
    - Node.js
    - npm
    - npx

1.2 Good practice for folders structure in React

Files outside the folders are generated and can be deleted. I left them to understand how React works. 

https://stackademic.com/blog/react-typescript-folder-structure-to-follow-ae614e786f8a

shortly:
##### 1. components - used in pages, e.g. Footer.tsx, UserProfile.tsx
- components
    - Footer
        - Footer.tsx - component file
        - index.tsx - single access point

##### 2. Hooks - in camel case
- hooks
    - yourCustomHook.tsx

##### 3. Pages - for organising components for views/pages
- pages
    - Home
        - Home.tsx
        - index.tsx

##### 4. Types - for type & enums definitions
- types
    - Footer.types.ts !!! **don't: */d/ts, because types will be on global scope, instead use *.types.ts and "export"**

##### 5. Utils - helper functions, shared across application
- utils
    - yourFunctionality.ts

##### 6. Api/services
- api
    - services
        - getFooterData.ts

##### 7. Constants
- constants
    - Footer.data.ts

##### 8. Assets - images(try .WebP, smaller in size than .JPG, JPEG, PNG) , icons(best SVG)
- assets
    - icons
    - images

##### 9. Styles - for global styles, variable names, mixins, functions(Sass)
- styles
    - _global.scss
    - _mixins_scss
    - _variables.scss
---------------------------
