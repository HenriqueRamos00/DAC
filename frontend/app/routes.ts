import { type RouteConfig, index, layout, route } from "@react-router/dev/routes";

export default [
  index("routes/home.tsx"),
  route("login", "routes/login.tsx"),
  route("/cadastro", "routes/cadastro.tsx"),
  layout("./layout/app-layout.tsx", [
    route("cliente", "routes/cliente/dashboard.tsx"),
  ])
] satisfies RouteConfig;

