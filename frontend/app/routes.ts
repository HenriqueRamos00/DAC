import { type RouteConfig, index, layout, route } from "@react-router/dev/routes";

export default [
  // index("routes/home.tsx"),
  layout("./layout/app-layout.tsx", [
    index("routes/home.tsx"),
    route("cliente", "routes/cliente/tela-inicial.tsx"),
  ])
] satisfies RouteConfig;

