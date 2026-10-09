import js from '@eslint/js';
import globals from 'globals';
import tseslint from 'typescript-eslint';
import reactHooks from 'eslint-plugin-react-hooks';
import reactRefresh from 'eslint-plugin-react-refresh';
import pluginQuery from '@tanstack/eslint-plugin-query';
import pluginRouter from '@tanstack/eslint-plugin-router';

const moduloPorIndex = {
  group: ['@/modules/*/*'],
  message: 'Importá un módulo solo desde su index (@/modules/<modulo>).',
};

const capasDeApp = {
  group: ['@/app/*', '@/routes/*', '@/layout/*', '@/routeTree.gen'],
  message: 'Esta capa no puede depender de app, routes ni layout.',
};

export default tseslint.config(
  { ignores: ['dist', 'public', 'src/routeTree.gen.ts'] },
  {
    files: ['**/*.{ts,tsx}'],
    extends: [
      js.configs.recommended,
      ...tseslint.configs.strict,
      ...pluginQuery.configs['flat/recommended'],
      ...pluginRouter.configs['flat/recommended'],
    ],
    languageOptions: { globals: globals.browser },
    plugins: { 'react-hooks': reactHooks, 'react-refresh': reactRefresh },
    rules: {
      ...reactHooks.configs.recommended.rules,
      'react-refresh/only-export-components': ['warn', { allowExportNames: ['Route'] }],
      '@typescript-eslint/consistent-type-imports': 'error',
      'no-restricted-imports': ['error', { patterns: [moduloPorIndex] }],
    },
  },
  {
    files: ['src/modules/**/*.{ts,tsx}'],
    rules: {
      'no-restricted-imports': [
        'error',
        {
          patterns: [
            moduloPorIndex,
            capasDeApp,
            { group: ['../../*'], message: 'Para usar otro módulo importá @/modules/<modulo>.' },
          ],
        },
      ],
    },
  },
  {
    files: ['src/shared/**/*.{ts,tsx}'],
    rules: {
      'no-restricted-imports': [
        'error',
        {
          patterns: [
            capasDeApp,
            { group: ['@/modules/*', '@/mocks/*'], message: 'shared no conoce módulos ni mocks.' },
          ],
        },
      ],
    },
  },
  {
    files: ['**/*.test.{ts,tsx}', 'src/test/**'],
    rules: { 'no-restricted-imports': 'off' },
  },
);
