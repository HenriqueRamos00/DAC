import { type RouteConfig, index, layout, route } from "@react-router/dev/routes";

export default [
  //route("login", "routes/login.tsx"), 
  layout("./layout/app-layout.tsx", [
    index("routes/home.tsx"),
    route("cliente", "routes/cliente/dashboard.tsx"),
  ])
] satisfies RouteConfig;

